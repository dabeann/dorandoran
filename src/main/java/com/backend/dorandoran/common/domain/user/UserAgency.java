package com.backend.dorandoran.common.domain.user;

import lombok.Getter;

@Getter
public enum UserAgency {

    VISION_TRAINING_CENTER("서울특별시립비전트레이닝센터", "01040786469"),
    GUEST_HOUSE("서울특별시립24시간게스트하우스", "01040786469"),
    RESTANDING("다시서기종합지원센터", "01040786469"),
    MANNASEM("만나샘", "01040786469"),
    BRIDGE("브릿지종합지원센터", "01040786469"),
    BOHYEON("영등포보현종합지원센터", "01040786469"),
    ONGDALSAM_DROPIN_CENTER("옹달샘드롭인센터", "01040786469"),
    SUNSHINE("햇살보금자리", "01040786469"),
    SOJOONGHAN("소중한사람들", "01040786469"),
    CANAAN_SHELTER("가나안쉼터", "01040786469"),
    AEWON_HOME_OF_HOPE("애원희망홈", "01040786469"),
    YES_NANUM("아침을여는집", "01040786469"),
    CHEONAEWON_HOUSE_OF_HOPE("천애원희망의집", "01040786469"),
    HEUINDONHUI("흰돌회", "01040786469"),
    JABINANUM("수송보현의집", "01040786469"),
    HOPE_OF_TREE("희망나무", "01040786469"),
    SEODAEMUN_SARANGBANG("서대문사랑방", "01040786469"),
    OPEN_WOMENS_CENTER("열린여성센터", "01040786469"),
    GAJEOUL_SHELTER("구세군가재울쉼터", "01040786469"),
    GILGAON_HYEMYEONG("길가온혜명", "01040786469"),
    GWANGYA_HOMELESS_CENTER("광야홈리스센터", "01040786469"),
    KOREA_EPISCOPAL_RESIDENCE("대한성공회살림터", "01040786469"),
    GANGDONG_HOUSE_OF_HOPE("강동희망의집", "01040786469"),
    ETC("기타", "01040786469");

    private final String koreanName;
    private final String phoneNumber;

    UserAgency(String koreanName, String phoneNumber) {
        this.koreanName = koreanName;
        this.phoneNumber = phoneNumber;
    }
}
