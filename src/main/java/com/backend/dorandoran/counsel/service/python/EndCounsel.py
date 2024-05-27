# -*- coding: utf-8 -*-
import sys
#print(sys.path)

import warnings

warnings.filterwarnings("ignore", category=DeprecationWarning)

import os
from dotenv import load_dotenv
import traceback
import psycopg2
from psycopg2.extras import RealDictCursor
import openai
from langchain_openai import OpenAI

load_dotenv()

openai.api_key = os.getenv('OPENAI_API_KEY')
sys.stdout.reconfigure(encoding='utf-8')

llm = OpenAI(
    temperature=0,
    openai_api_key=openai.api_key,
    model_name=os.getenv('MODEL_NAME')
)


def generate_chat_summary(counsel_id):
    # PostgreSQL 데이터베이스 연결 설정
    conn = psycopg2.connect(
        user=os.getenv('DATASOURCE_USERNAME'),
        password=os.getenv('DATASOURCE_PASSWORD'),
        host=os.getenv('DATASOURCE_HOST'),
        port=os.getenv('DATASOURCE_PORT'),
        dbname=os.getenv('DATASOURCE_DBNAME')
    )
    cursor = conn.cursor(cursor_factory=RealDictCursor)

    try:
        # 이전 대화 내역 조회
        cursor.execute("""
            SELECT role, contents FROM dialog
            WHERE counsel_id = %s
            ORDER BY created_date_time ASC
        """, (counsel_id,))
        previous_conversations = cursor.fetchall()

        # 전체 대화 내역을 history로 저장
        history = [
            {"role": conv["role"], "content": conv["contents"]} for conv in previous_conversations
        ]
        messages = [{"role": "system", "content": "다음은 내담자와 상담자의 대화입니다. "
                                                  "이 대화의 주요 내용을 150글자 이내로 간단히 요약해주세요.:" + str(history)}]

        # GPT 모델에게 요약 요청
        response = openai.chat.completions.create(
            model=os.getenv('MODEL_NAME'),
            messages=messages,
            max_tokens=150,
            temperature=0.7,
            n=1,
            stop=None
        )

        summary = response.choices[0].message.content

        # counsel 테이블의 summary 컬럼 업데이트
        cursor.execute("""
            UPDATE counsel
            SET summary = %s
            WHERE counsel_id = %s
        """, (summary, counsel_id))

        conn.commit()

        # 심리점수 내기
       # messages = [{"role": "system", "content": "점수내줘 어쩌고 뒤에는 대화 내역이야" + str(history)}]

        # GPT 모델에게 점수 추출 요구
        # 프롬포트 수정 필요
       # response = openai.chat.completions.create(
       #     model=os.getenv('MODEL_NAME'),
       #     messages=messages,
       #     max_tokens=150,
       #     temperature=0.7,
       #     n=1,
       #     stop=None
       # ).choices[0].message.content
        #1,2,3이 기본 형식이고 만약 숫자랑 , 말고 다른거 나오면 처리해야됨

        return summary

    except Exception as e:
        error_message = "Error: {}\n{}".format(e, traceback.format_exc())
        print(error_message)
        conn.rollback()
        return str(error_message)

    finally:
        cursor.close()
        conn.close()

def main(counsel_id):
    response = generate_chat_summary(counsel_id)
    print(response)

if __name__ == "__main__":
    counsel_id = sys.argv[1]
    main(counsel_id)
