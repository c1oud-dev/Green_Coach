package com.greencoach.service

import com.greencoach.model.SearchResultDto
import com.greencoach.model.StepSectionDto
import com.greencoach.model.SubCategoryDetailDto
import org.springframework.stereotype.Service

@Service
class SubCategoryDetailService {

    // 간단한 별칭/키워드 매핑
private val aliasToKey: Map<String, String> = mapOf(
        "투명 페트병" to "pet",
        "플라스틱" to "plastic",
        "비닐류" to "bag",
        "스티로폼" to "styro",
        "캔류" to "can",
        "고철류" to "steel",
        "유리병" to "glass",
        "종이류" to "paper",
        "섬유류" to "cloth",
        "소형 전자제품" to "small",
        "대형 전자제품" to "large",
        "가구" to "furniture",
        "전지류" to "battery",
        "음식물" to "food",
    )

fun search(keyword: String): SearchResultDto? {
        val q = keyword.trim().lowercase()
        // 완전/부분 일치 허용
        val matchedKey = aliasToKey.entries.firstOrNull {
                val k = it.key.lowercase()
                k == q || k.contains(q) || q.contains(k)
            }?.value ?: return null
        val detail = getDetail(matchedKey) ?: return null
        return SearchResultDto(key = detail.key, name = detail.name)
    }
    fun getDetail(key: String): SubCategoryDetailDto? = when (key) {
        "pet" -> SubCategoryDetailDto(
            key = key, name = "투명 페트병",
            imageUrl = "/images/sub/pet.png",
            headerColor = "#66CBD2",
            subtitle = "투명 페트병은 고품질 재활용이 가능한 자원이기 때문에, 이물질 없이 깨끗하게 분리배출하는 것이 중요합니다.",
            steps = listOf(
                StepSectionDto("내용물 비우기 & 헹구기", listOf(
                    "남은 음료 제거 → 가볍게 헹굼",
                    "우유·막걸리·기름 잔류 시 전용함 불가"
                )),
                StepSectionDto("라벨·목링 제거", listOf(
                    "수축라벨·목링까지 떼어 분리(라벨은 플라스틱류)"
                )),
                StepSectionDto("찌그러뜨리고 뚜껑 닫기", listOf(
                    "공기 빼서 압착 후 뚜껑 닫아 배출(선별 단계에서 재질 분리)"
                ))
            ),
            wrongExamples = listOf(
                "갈색·녹색 페트병을 투명 페트 전용함에 투입",
                "라벨/목링을 떼지 않음, 또는 내용물이 남은 상태로 배출",
                "우유·막걸리·기름 잔류 병을 전용함에 배출",
                "직접 인쇄된 무색 병을 전용함에 투입",
                "일회용 투명컵(PET/PP)을 전용함에 투입",
                "병을 압착하지 않고 부피 큰 상태로 배출"
            )
        )

        "plastic" -> SubCategoryDetailDto(
            key = key, name = "플라스틱",
            imageUrl = "/images/sub/plastic.png",
            headerColor = "#66CBD2",
            subtitle = "플라스틱 포장재·용기는 재질 혼합(라벨·펌프·금속 스프링 등)과 오염 여부에 따라 재활용 품질이 크게 달라집니다.\n" +
                    " 세척과 부착물 분리가 핵심입니다.",
            steps = listOf(
                StepSectionDto("내용물 비우기 & 헹구기", listOf(
                    "음식물·기름기 제거(오염 심하면 종량제 가능)"
                )),
                StepSectionDto("부착물 분리", listOf(
                    "라벨·알루미 캡·펌프(금속 스프링)·종이 껍데기 등 이물질 분리"
                )),
                StepSectionDto("부피 줄이기", listOf(
                    "가능한 범위에서 압착/겹쳐 배출, 뚜껑은 닫아 분실 방지"
                ))
            ),
            wrongExamples = listOf(
                "무색 생수·음료 투명 페트병을 일반 플라스틱으로 배출",
                "비닐·랩·스티로폼을 일반 플라스틱과 혼합 배출",
                "기름·양념 잔류 용기를 세척 없이 배출",
                "펌프/분사 금속 스프링 부품을 분리하지 않은 채 배출",
                "알루미 캡·종이 라벨 등 이물질을 제거하지 않고 배출",
            )
        )

        "bag" -> SubCategoryDetailDto(
            key = key, name = "비닐류",
            imageUrl = "/images/sub/bag.png",
            headerColor = "#66CBD2",
            subtitle = "병·용기 같은 경질 플라스틱과 달리, 얇고 유연한 ‘필름형 포장재’를 비닐류로 분리합니다. 이물·수분이 남아 있으면 선별·재활용 품질이 크게 저하됩니다.",
            steps = listOf(
                StepSectionDto("비우고 가볍게 헹군 뒤 물기 제거", listOf(
                )),
                StepSectionDto("테이프·라벨·철사 타이·지퍼 슬라이더 등 이물질 분리", listOf(
                )),
                StepSectionDto("한 봉투에 모아 묶어 배출(날림·비산 방지)", listOf(
                ))
            ),
            wrongExamples = listOf(
                "소스·기름 묻은 과자/라면 봉지를 세척 없이 비닐류에 투입",
                "테이프·라벨·지퍼 슬라이더를 제거하지 않고 배출",
                "젖은 랩·습기 많은 필름을 말리지 않고 투입",
                "은박 라미네이트 포장을 대량으로 비닐류에 투입(재활용 곤란)",
            )
        )

        "styro" -> SubCategoryDetailDto(
            key = key, name = "스티로폼",
            imageUrl = "/images/sub/styro.png",
            headerColor = "#66CBD2",
            subtitle = "스티로폼은 발포된 폴리스티렌(EPS)으로, 포장재 중심만 분리수거 대상이에요. 음식물·기름·라미네이트 코팅이 있으면 재활용이 어렵습니다.",
            steps = listOf(
                StepSectionDto("이물질 제거·건조", listOf(
                    "내용물·수분·이물질 제거, 깨끗하고 마른 상태로"
                )),
                StepSectionDto("부착물 분리", listOf(
                    "테이프·스티커·비닐 랩·철사/케이블타이 완전 제거"
                )),
                StepSectionDto("묶어서 배출", listOf(
                    "잘게 부수지 말고 끈으로 묶거나 투명 봉투에 담아 비산 방지"
                ))
            ),
            wrongExamples = listOf(
                "컵라면 용기를 스티로폼으로 배출",
                "테이프·스티커·랩을 제거하지 않고 배출",
                "수분/음식물이 남아 젖은 상태로 배출",
                "스티로폼을 잘게 부숴 흩어진 채로 배출(비산·오염 유발)"
            )
        )

        "can" -> SubCategoryDetailDto(
            key = key, name = "캔류",
            imageUrl = "/images/sub/can.png",
            headerColor = "#66CBD2",
            subtitle = "철(Fe)·알루미늄 금속 포장재는 스크랩 재활용이 가능하므로 내용물·이물 제거가 핵심입니다.",
            steps = listOf(
                StepSectionDto("내용물 비우기 & 헹구기", listOf(
                    "국물/기름 제거, 물기 털기"
                )),
                StepSectionDto("부착물 분리", listOf(
                    "플라스틱 뚜껑·라벨·종이 포장, 고무 패킹 등 이물질 제거"
                )),
                StepSectionDto("뚜껑 안전 처리", listOf(
                    "캔 가장자리가 날카로우면 안쪽으로 말아 넣거나 완전 분리해 안전 배치"
                )),
                StepSectionDto("부피 줄이기", listOf(
                    "가능하면 발로 눌러 평평하게(알루미늄 캔)"
                ))
            ),
            wrongExamples = listOf(
                "가스 완전 배출 안 된 부탄캔/스프레이를 캔류에 투입",
                "기름기·음식물 잔존 캔을 세척 없이 배출",
                "은박 파우치(복합재)를 캔류로 배출",
                "냄비·프라이팬 같은 고철류를 캔류에 혼합",
                "날카로운 뚜껑을 밖으로 노출한 채 배출(안전 위험)"
            )
        )

        "steel" -> SubCategoryDetailDto(
            key = key, name = "고철류",
            imageUrl = "/images/sub/steel.png",
            headerColor = "#66CBD2",
            subtitle = "캔류(음료·통조림)와 달리 제품성 금속(철·스테인리스 등)을 모아 배출하는 항목이에요. 비금속 부품을 분리하고 안전하게 묶는 것이 핵심입니다.",
            steps = listOf(
                StepSectionDto("세척·이물 제거", listOf(
                    "음식물/기름, 먼지·테이프 제거"
                )),
                StepSectionDto("비금속 분리", listOf(
                    "손잡이(플라스틱/목재), 유리 뚜껑·고무패킹 등 분리"
                )),
                StepSectionDto("안전 포장", listOf(
                    "날이 있는 품목(칼·톱·절단 철판)은 신문지/테이프로 감싸 표기"
                )),
                StepSectionDto("크기별 배출", listOf(
                    "소형은 묶음/상자에 담아 배출, 대형은 예약·스티커 부착"
                ))
            ),
            wrongExamples = listOf(
                "자전거·철제 선반을 무단 배출(대형폐기물 신고 누락)",
                "플라스틱 손잡이·유리 뚜껑을 떼지 않고 그대로 배출",
                "날카로운 칼·톱을 포장 없이 배출(안전 위험)",
                "캔류(음료캔·통조림)와 혼합 배출"
            )
        )

        "glass" -> SubCategoryDetailDto(
            key = key, name = "유리병",
            imageUrl = "/images/sub/glass.png",
            headerColor = "#66CBD2",
            subtitle = "유리 병 형태의 용기만 대상. 내용물·기름기 제거와 뚜껑 분리가 핵심이며, 깨진 유리·내열/강화유리·도자기·거울은 해당이 되지 않습니다.",
            steps = listOf(
                StepSectionDto("비우고 헹구기", listOf(
                    "내용물·기름 제거, 물기 털기"
                )),
                StepSectionDto("부착물 분리", listOf(
                    "금속/플라스틱 뚜껑·캡·폼마개, 알루미 포일, 실리콘 마개 등 분리"
                )),
                StepSectionDto("라벨", listOf(
                    "가능하면 제거(접착 강하면 무리 X)"
                )),
                StepSectionDto("보증금 병", listOf(
                    "가능하면 소매점 반납(보증금 환불)"
                ))
            ),
            wrongExamples = listOf(
                "깨진 유리를 유리병류에 그대로 투입(안전·선별 문제)",
                "뚜껑·폼마개를 분리하지 않고 통째로 배출",
                "기름·소스 잔류 병을 세척 없이 배출",
                "내열/강화유리·도자기를 유리병류로 혼합 배출",
                "보증금 대상 병을 일반 배출(반납 시 환불 가능)"
            )
        )

        "paper" -> SubCategoryDetailDto(
            key = key, name = "종이류",
            imageUrl = "/images/sub/paper.png",
            headerColor = "#66CBD2",
            subtitle = "종이섬유 재활용 품질은 수분·오염·이물(테이프/스프링/라벨) 제거에 좌우돼요. 펼쳐 접고, 묶어서 배출하면 선별이 좋아집니다.",
            steps = listOf(
                StepSectionDto("비우고 건조 ", listOf(
                    "물기·오염 제거, 젖은 종이는 말린 뒤 분류"
                )),
                StepSectionDto("이물 분리 ", listOf(
                    "테이프·스티커·철제 스프링·플라스틱 표지 분리"
                )),
                StepSectionDto("펼치고 묶기 ", listOf(
                    "상자는 펼쳐 납작하게, 신문/책은 끈으로 묶어 배출"
                ))
            ),
            wrongExamples = listOf(
                "피자·치킨 박스에 기름 오염이 심한데 종이류로 배출",
                "감열지 영수증/코팅 포장지를 종이류에 혼합",
                "테이프·라벨·스프링을 떼지 않고 그대로 배출",
                "젖은 종이를 건조 없이 바로 투입",
                "종이팩/종이컵을 종이류로 함께 배출(전용 체계 이용 필요)"
            )
        )

        "cloth" -> SubCategoryDetailDto(
            key = key, name = "섬유류",
            imageUrl = "/images/sub/cloth.png",
            headerColor = "#66CBD2",
            subtitle = "의류수거함은 재사용 중심으로 운영돼서, 입기 어려운 상태·젖은·심하게 오염된 품목은 대상이 아닙니다. 침구류(이불·베개·쿠션·커튼 등)는 보통 별도 체계(특수마대/대형폐기물)로 처리해야 합니다.",
            steps = listOf(
                StepSectionDto("세탁·완전 건조 후 배출(냄새·곰팡이 방지)", listOf(
                )),
                StepSectionDto("쌍(한 켤레) 묶기·단추/지퍼 잠그기, 주머니 비우기(개인정보 제거)", listOf(
                )),
                StepSectionDto("잡화 분리", listOf(
                    "금속 장식·플라스틱 행거 등 이물은 가능하면 제거"
                )),
                StepSectionDto("수거함 안내 준수", listOf(
                    "“신발/가방 허용 여부”, “운영 시간/장소” 확인"
                ))
            ),
            wrongExamples = listOf(
                "젖은/곰팡이 난 의류를 수거함에 투입(재사용 불가) 서울시 뉴스",
                "속옷·심하게 손상된 옷을 수거함에 투입(일반쓰레기 대상) 서울시 뉴스",
                "이불·베개·쿠션을 수거함에 넣음(대형/특수마대 대상) 서울시 뉴스",
                "한 짝만 남은 신발·가방 내용물 미제거 상태로 배출(분실·이물 혼입)"
            )
        )

        "small" -> SubCategoryDetailDto(
            key = key, name = "소형 전자제품",
            imageUrl = "/images/sub/small.png",
            headerColor = "#66CBD2",
            subtitle = "소형 전자제품은 일반 배출 금지. 동(里) 주민센터·지자체 거점의 전용 수거함/수거일을 이용하거나, 지자체 이동수거·택배수거 등 전용 회수체계를 사용.",
            steps = listOf(
                StepSectionDto("전원·배터리 분리", listOf(
                    "내장/분리형 배터리는 가능한 한 분리 → 전지류 수거함(리튬 등 단자 테이핑)"
                )),
                StepSectionDto("개인정보 삭제", listOf(
                    "공장초기화/로그아웃, USIM·SD카드 제거"
                )),
                StepSectionDto("부속 분리·정리", listOf(
                    "케이블·충전기는 한데 묶어 동봉, 금속 날·유리부품은 안전 포장"
                )),
                StepSectionDto("수거 방식 확인", listOf(
                    "거점 투입/수거일 배출/지자체 예약 등 지역 안내 준수"
                ))
            ),
            wrongExamples = listOf(
                "배터리를 분리하지 않은 휴대폰·면도기를 전용함에 투입",
                "초기화/로그아웃 없이 스마트기기를 배출(개인정보 유출 위험)",
                "배터리 단독을 소형 전자제품함에 투입(→ 전지류로 분리)",
                "전선·충전기를 흩어져 배출(선별 불편·분실)",
                "깨진 화면·날카로운 부품을 무포장 배출(안전 위험)"
            )
        )

        "large" -> SubCategoryDetailDto(
            key = key, name = "대형 전자제품",
            imageUrl = "/images/sub/large.png",
            headerColor = "#66CBD2",
            subtitle = "대형 전자제품은 일반 배출 금지이며, 전용 회수체계(무상 방문수거 또는 판매자 회수)를 이용해야 합니다. 냉매·유해부품 처리 등 전문 수거·해체가 전제.",
            steps = listOf(
                StepSectionDto("수거 예약", listOf(
                    "무상 방문수거 웹/전화 예약 또는 신제품 배송 시 1:1 회수 요청"
                )),
                StepSectionDto("사전 준비", listOf(
                    ": 내용물 비우기·선반 고정, 전원 분리, 물·배수 호스 정리(냉장고 성에 제거)"
                )),
                StepSectionDto("안전 이동 준비", listOf(
                    "문/서랍 테이프로 고정, 설치·배관 분리는 가능하면 기사에게 요청"
                )),
                StepSectionDto("개인정보 삭제", listOf(
                    "스마트TV·인터넷 기기 등은 계정 로그아웃·초기화"
                ))
            ),
            wrongExamples = listOf(
                "아파트 분리수거장에 냉장고·세탁기를 무단 배출",
                "신제품 배송 시 1:1 회수 요청 없이 현관 앞 방치",
                "음식물·물·얼음이 남은 냉장고를 그대로 내놓음(누수·악취)",
                "에어컨 실외기 미포함 또는 임의 철거(안전사고 위험)",
                "스마트TV를 계정/저장 데이터 삭제 없이 배출"
            )
        )

        "furniture" -> SubCategoryDetailDto(
            key = key, name = "가구",
            imageUrl = "/images/sub/furniture.png",
            headerColor = "#66CBD2",
            subtitle = "가구는 대형폐기물로 분류되어 일반 분리배출 장소(재활용장) 배출 금지예요. 온라인/전화로 대형폐기물 신고·수수료 납부 → 지정 일자·장소 배출 절차가 원칙이에요.",
            steps = listOf(
                StepSectionDto("신고/예약", listOf(
                    "지자체 대형폐기물 웹/앱/전화 신고 → 품목·규격 기입, 수수료 납부(스티커/QR)"
                )),
                StepSectionDto("분해·이물 분리", listOf(
                    "발/다리·유리·금속 프레임 분리, 서랍·선반 비우기"
                )),
                StepSectionDto("안전 포장", listOf(
                    "유리/거울은 신문지+테이프로 표면 X자 테이핑, 날카로운 모서리 보호"
                )),
                StepSectionDto("배출", listOf(
                    "지정 날짜·장소에 스티커/QR 부착 후 내놓기(비·눈 대비 방수 포장)"
                ))
            ),
            wrongExamples = listOf(
                "아파트 분리수거장에 책장·소파를 무단 배출",
                "스티커/QR 미부착 상태로 지정일 전날 임의 배출",
                "유리문·거울을 포장 없이 그대로 배출(안전 위험)",
                "서랍·수납물 미비움, 금속·전선·조명 부품 분리 없이 배출"
            )
        )

        "battery" -> SubCategoryDetailDto(
            key = key, name = "전지류",
            imageUrl = "/images/sub/battery.png",
            headerColor = "#66CBD2",
            subtitle = "전지는 금속·전해질로 화재·누액 위험이 있어 일반쓰레기 혼합 금지. 특히 리튬이온은 단락(+)·(–) 접촉 시 화재 위험이 큼.",
            steps = listOf(
                StepSectionDto("분리", listOf(
                    "가능한 경우 기기에서 배터리 분리(분리 불가면 소형 전자제품 체계 이용)"
                )),
                StepSectionDto("절연", listOf(
                    "9V·리튬이온·단추형 등은 단자 하나씩 절연 테이프 부착"
                )),
                StepSectionDto("모음·포장", listOf(
                    "종류별로 모아 투명봉투/소형 상자에 담아 누액·단락 방지"
                )),
                StepSectionDto("전용 수거함 배출", listOf(
                    "주민센터·거점함·판매점 회수함 등 전용 회수체계 이용"
                )),
                StepSectionDto("파손·팽창 배터리", listOf(
                    "구멍 내지 말고 내열 용기(모래/비가연성 용기)에 임시 보관 후 지자체 안내에 따름"
                ))
            ),
            wrongExamples = listOf(
                "배터리를 절연(테이핑) 없이 한 봉지에 섞어 투입",
                "일반쓰레기로 배출하거나 불에 태움(화재·유해가스 위험)",
                "팽창·파손된 리튬이온을 찌그러뜨리거나 구멍 냄",
                "전자제품 본체에 배터리를 꽂은 채 소형 전자제품함에 투입",
                "서로 극이 맞닿게 포개어 묶음(단락 위험)"
            )
        )

        "food" -> SubCategoryDetailDto(
            key = key, name = "음식물 쓰레기",
            imageUrl = "/images/sub/food.png",
            headerColor = "#66CBD2",
            subtitle = "음식물류 폐기물은 사료·퇴비 등 자원화가 목적이라 수분·이물 제거가 중요해요. 전용 수거함(종량제 봉투/RFID계량기 등)으로 타 품목과 혼합 없이 배출합니다.\n",
            steps = listOf(
                StepSectionDto("물기·이물 제거", listOf(
                    "국물은 따라 버리고, 뼈·껍데기·포장재 등 이물질 제거"
                )),
                StepSectionDto("잘게 썰어 부피 줄이기", listOf(
                    "통무·통배추·수박껍질 등은 잘게 자른 뒤 배출(설비 고장 예방)"
                )),
                StepSectionDto("전용 용기 사용", listOf(
                    "지역별 음식물 종량제 봉투 / 전용 수거함(RFID 등) 규정에 맞춰 배출"
                ))
            ),
            wrongExamples = listOf(
                "조개껍데기·달걀껍데기·큰 뼈를 음식물 수거함에 투입",
                "국물/수분 제거 없이 그대로 배출(악취·처리비용 증가)",
                "티백·커피찌꺼기·한약재 등 비(非)음식물류를 혼합",
                "비닐·젖은 휴지·나무젓가락·이쑤시개 등 이물질과 섞어서 배출"
            )
        )
        else -> null
    }
}