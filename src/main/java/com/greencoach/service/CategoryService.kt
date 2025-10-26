package com.greencoach.service

import com.greencoach.model.CategoryDto
import org.springframework.stereotype.Service

@Service
class CategoryService {
    // 1) 최상위 카테고리 목록
    private val topCategories = listOf(
        CategoryDto("투명 페트병", "/images/icons/pet_bottle.png"),
        CategoryDto("플라스틱",   "/images/icons/plastic_container.png"),
        CategoryDto("비닐류",     "/images/icons/bag.png"),
        CategoryDto("스티로폼",   "/images/icons/styrofoam.png"),
        CategoryDto("캔류",       "/images/icons/can.png"),
        CategoryDto("고철류",     "/images/icons/steel.png"),
        CategoryDto("유리병",     "/images/icons/glass_bottle.png"),
        CategoryDto("종이류",     "/images/icons/paper.png"),
        CategoryDto("섬유류",  "/images/icons/fabric.png"),
        CategoryDto("소형 전자제품", "/images/icons/small_appliance.png"),
        CategoryDto("대형 전자제품", "/images/icons/large_appliance.png"),
        CategoryDto("가구",       "/images/icons/furniture.png"),
        CategoryDto("전지류",     "/images/icons/battery.png"),
        CategoryDto("음식물",     "/images/icons/food.png"),
    )

    // 2) 서브 카테고리 매핑
    private val subCategoriesMap: Map<String, List<CategoryDto>> = mapOf(
        "투명 페트병" to listOf(
            CategoryDto("생수",     "/images/sub/pet_water.png"),
            CategoryDto("음료수",   "/images/sub/pet_drink.png"),
            CategoryDto("막걸리",   "/images/sub/pet_makgeolli.png"),
            CategoryDto("사이다",   "/images/sub/pet_sprite.jpg"),
            CategoryDto("이온 음료",   "/images/sub/pet_sports.png"),
            CategoryDto("탄산수",   "/images/sub/pet_sparkling.webp"),
        ),
        "플라스틱" to listOf(
            CategoryDto("도시락 용기",     "/images/sub/plastic_lunchbox.jpg"),
            CategoryDto("샴푸",   "/images/sub/plastic_shampoo.jpg"),
            CategoryDto("세제",   "/images/sub/plastic_detergent.jpg"),
            CategoryDto("식용유",   "/images/sub/plastic_oil.jpg"),
            CategoryDto("일회용 컵",   "/images/sub/plastic_cup.jpg"),
            CategoryDto("케첩",   "/images/sub/plastic_ketchup.jpg"),
        ),
        "비닐류" to listOf(
            CategoryDto("비닐 봉투",     "/images/sub/vinyl_bag.jpg"),
            CategoryDto("과자",   "/images/sub/vinyl_snack.jpg"),
            CategoryDto("라면",   "/images/sub/vinyl_ramen.jpg"),
            CategoryDto("랩",   "/images/sub/vinyl_wrap.jpg"),
            CategoryDto("지퍼백",   "/images/sub/vinyl_zipper.jpg"),
            CategoryDto("에어캡",   "/images/sub/vinyl_bubble.jpg"),
        ),
        "스티로폼" to listOf(
            CategoryDto("아이스박스",     "/images/sub/styro_bag.png"),
            CategoryDto("완충재",   "/images/sub/styro_buffer.jpg"),
            CategoryDto("음식 트레이",   "/images/sub/styro_psp.jpg"),
        ),
        "캔류" to listOf(
            CategoryDto("음료",     "/images/sub/can_drink.jpg"),
            CategoryDto("통조림",   "/images/sub/can_food.png"),
            CategoryDto("호일",   "/images/sub/can_foil.png"),
            CategoryDto("스프레이",   "/images/sub/can_spray.jpg"),
            CategoryDto("부탄가스",   "/images/sub/can_gas.jpg"),
        ),
        "고철류" to listOf(
            CategoryDto("냄비",     "/images/sub/steel_pot.jpg"),
            CategoryDto("프라이팬",   "/images/sub/steel_pan.png"),
            CategoryDto("나사",   "/images/sub/steel_screw.jpg"),
            CategoryDto("옷걸이",   "/images/sub/steel_hanger.jpg"),
            CategoryDto("집게",   "/images/sub/steel_tongs.jpg"),
            CategoryDto("국자",   "/images/sub/steel_ladle.png"),
            CategoryDto("자전거",   "/images/sub/steel_bicycle.jpg"),
        ),
        "유리병" to listOf(
            CategoryDto("술",     "/images/sub/glass_alcohol.jpg"),
            CategoryDto("와인",   "/images/sub/glass_wine.png"),
            CategoryDto("잼",   "/images/sub/glass_jam.png"),
            CategoryDto("화장품",   "/images/sub/glass_cosmetics.jpg"),
        ),
        "종이류" to listOf(
            CategoryDto("박스",     "/images/sub/paper_box.png"),
            CategoryDto("노트",   "/images/sub/paper_note.jpg"),
            CategoryDto("신문",   "/images/sub/paper_news.jpg"),
            CategoryDto("종이봉투",   "/images/sub/paper_bag.jpg"),
            CategoryDto("책",   "/images/sub/paper_book.jpg"),
            CategoryDto("달력",   "/images/sub/paper_calendar.png"),
        ),
        "섬유류" to listOf(
            CategoryDto("옷",     "/images/sub/cloth_cloths.jpg"),
            CategoryDto("가방",   "/images/sub/cloth_bag.jpg"),
            CategoryDto("모자",   "/images/sub/cloth_cap.jpg"),
            CategoryDto("신발",   "/images/sub/cloth_shoes.png"),
            CategoryDto("벨트",   "/images/sub/cloth_belt.png"),
        ),
        "대형 전자제품" to listOf(
            CategoryDto("냉장고",     "/images/sub/large_refrigerator.jpg"),
            CategoryDto("세탁기",   "/images/sub/large_washing.png"),
            CategoryDto("에어컨",   "/images/sub/large_air.jpg"),
            CategoryDto("전자레인지",   "/images/sub/large_microwave.jpg"),
            CategoryDto("청소기",   "/images/sub/large_vacuum.png"),
            CategoryDto("TV",   "/images/sub/large_tv.jpg"),
        ),
        "소형 전자제품" to listOf(
            CategoryDto("드라이기",     "/images/sub/small_dryer.png"),
            CategoryDto("마우스",   "/images/sub/small_mouse.png"),
            CategoryDto("면도기",   "/images/sub/small_razor.jpeg"),
            CategoryDto("이어폰",   "/images/sub/small_earphones.png"),
            CategoryDto("휴대폰",   "/images/sub/small_phone.png"),
            CategoryDto("충전기",   "/images/sub/small_cable.png"),
        ),
        "가구" to listOf(
            CategoryDto("소파",     "/images/sub/furniture_sofa.png"),
            CategoryDto("전신 거울",   "/images/sub/furniture_mirror.jpg"),
            CategoryDto("책상",   "/images/sub/furniture_desk.jpg"),
            CategoryDto("의자",   "/images/sub/furniture_chair.png"),
        ),
        "전지류" to listOf(
            CategoryDto("건전지",     "/images/sub/battery_battery.jpg"),
            CategoryDto("보조 배터리",   "/images/sub/battery_supplementary.jpg"),
            CategoryDto("코인셀",   "/images/sub/battery_coin.jpg"),
        ),
    )

    /** 모든 최상위 카테고리 반환 */
    fun getTopCategories(): List<CategoryDto> =
        topCategories

    /**
     * 선택된 카테고리에 대한 서브 카테고리 반환
     * @param categoryName topCategories 중 하나의 name
     */
    fun getSubCategories(categoryName: String): List<CategoryDto> =
        subCategoriesMap[categoryName] ?: emptyList()
}