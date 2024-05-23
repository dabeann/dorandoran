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
from datetime import datetime
import openai
from langchain_openai import OpenAI

load_dotenv()

openai.api_key = os.getenv('OPENAI_API_KEY')


llm = OpenAI(
    temperature=0,
    openai_api_key=openai.api_key,
    model_name=os.getenv('MODEL_NAME')
)


def get_chat_response(consultation_id, user_message):
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
        """, (consultation_id,))
        previous_conversations = cursor.fetchall()

        # 전체 대화 내역을 history로 저장
        history = [
            {"role": conv["role"], "content": conv["contents"]} for conv in previous_conversations
        ]

        # 확인용
        print("history:", history)

        # counsel_id를 사용하여 user_id 가져오기
        cursor.execute("SELECT user_id FROM counsel WHERE counsel_id = %s", (consultation_id,))
        result = cursor.fetchone()
        user_id = result['user_id']

        # name 가져오기
        cursor.execute('SELECT "name" FROM "user" WHERE user_id = %s', (user_id,))
        result = cursor.fetchone()
        user_name = result['name']

        # GPT 모델에게 메시지 전달
        gpt_message = openai.chat.completions.create(
            model=os.getenv('MODEL_NAME'),
            messages=[
                {"role": "system", "content": "당신은 노숙인을 대상으로 심리상담을 제공하는 상담봇입니다." 
                                                f"노숙인 정보 : 이름 : {user_name}., "
                                                f"미러링, 자기공개, "
                                                f"요약 등을 통해 심리상담합니다. 제공한 노숙인 정보를 바탕으로 "
                                                f"개인화된 상담을 진행합니다. 대화중에 내담자, "
                                                f"당신 단어 대신에 노숙인의 이름을 말해줍니다.."},
                {"role": "assistant", "content": str(history)},
                {"role": "user", "content": user_message}
            ]
        ).choices[0].message.content

        # 사용자 메시지 데이터베이스에 저장
        cursor.execute("""
            INSERT INTO dialog (counsel_id, role, contents, created_date_time)
            VALUES (%s, %s, %s, %s)
        """, (consultation_id, 'FROM_USER', user_message, datetime.utcnow()))
        conn.commit()

        # GPT-3 응답 데이터베이스에 저장
        cursor.execute("""
            INSERT INTO dialog (counsel_id, role, contents, created_date_time)
            VALUES (%s, %s, %s, %s)
        """, (consultation_id, 'FROM_CONSULTANT', gpt_message, datetime.utcnow()))
        conn.commit()

        return gpt_message

    except Exception as e:
        error_message = "Error: {}\n{}".format(e, traceback.format_exc())
        print(error_message)
        conn.rollback()
        return str(error_message)

    finally:
        cursor.close()
        conn.close()

def main(consultation_id, user_message):
    response = get_chat_response(consultation_id, user_message)
    print(response)

if __name__ == "__main__":
    consultation_id = sys.argv[1]
    user_message = sys.argv[2]
    main(consultation_id, user_message)
