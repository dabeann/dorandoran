package com.backend.dorandoran.common.domain.user;

public enum UserAgency {

    VISION_TRAINING_CENTER("서울특별시립비전트레이닝센터"),
    GUEST_HOUSE("서울특별시립24시간게스트하우스"),
    RESTANDING("다시서기종합지원센터"),
    MANNASEM("만나샘"),
    BRIDGE("브릿지종합지원센터"),
    BOHYEON("영등포보현종합지원센터"),
    ONGDALSAM_DROPIN_CENTER("옹달샘드롭인센터"),
    SUNSHINE("햇살보금자리"),
    SOJOONGHAN("소중한사람들"),
    CANAAN_SHELTER("가나안쉼터"),
    AEWON_HOME_OF_HOPE("애원희망홈"),
    YES_NANUM("아침을여는집"),
    CHEONAEWON_HOUSE_OF_HOPE("천애원희망의집"),
    HEUINDONHUI("흰돌회"),
    JABINANUM("수송보현의집"),
    HOPE_OF_TREE("희망나무"),
    SEODAEMUN_SARANGBANG("서대문사랑방"),
    OPEN_WOMENS_CENTER("열린여성센터"),
    GAJEOUL_SHELTER("구세군가재울쉼터"),
    GILGAON_HYEMYEONG("길가온혜명"),
    GWANGYA_HOMELESS_CENTER("광야홈리스센터"),
    KOREA_EPISCOPAL_RESIDENCE("대한성공회살림터"),
    GANGDONG_HOUSE_OF_HOPE("강동희망의집"),
    ETC("기타");

    private final String koreanName;

    UserAgency(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
