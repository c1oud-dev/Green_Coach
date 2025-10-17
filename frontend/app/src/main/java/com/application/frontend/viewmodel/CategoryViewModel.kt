package com.application.frontend.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.BuildConfig
import com.application.frontend.R
import com.application.frontend.data.CategoryApi
import com.application.frontend.data.detail.DetailApi
import com.application.frontend.model.Category
import com.application.frontend.model.SearchResultDto
import com.application.frontend.model.SubCategoryDto
import com.application.frontend.model.SubCategoryIcons
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "CategoryAPI"
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val api: CategoryApi,
    private val detailApi: DetailApi,
) : ViewModel() {
    // 최상위/서브 공용 상태
    private val _top  = MutableStateFlow<List<Category>>(emptyList())
    val top: StateFlow<List<Category>>  = _top

    private val _subs = MutableStateFlow<List<Category>>(emptyList())
    val subs: StateFlow<List<Category>> = _subs

    // 이름 → 로컬 아이콘 매핑(아이콘은 프론트 리소스 사용)
    private val topIconMap = mapOf(
        "투명 페트병" to R.drawable.ic_pet,
        "플라스틱"   to R.drawable.ic_plastic,
        "비닐류"     to R.drawable.ic_bag,
        "스티로폼"   to R.drawable.ic_styro,
        "캔류"       to R.drawable.ic_can,
        "고철류"     to R.drawable.ic_steel,
        "유리병"     to R.drawable.ic_glass,
        "종이류"     to R.drawable.ic_paper,
        "옷/섬유류"  to R.drawable.ic_cloth,
        "소형 전자제품" to R.drawable.ic_small,
        "대형 전자제품" to R.drawable.ic_large,
        "가구"       to R.drawable.ic_furniture,
        "전지류"     to R.drawable.ic_battery,
        "음식물"     to R.drawable.ic_food,
    )

    fun loadTop() {
        viewModelScope.launch {
            runCatching { api.getTopCategories() }               // List<SubCategoryDto> 재사용
                .onSuccess { remote ->
                    _top.value = remote.map { dto ->
                        val icon = topIconMap[dto.name] ?: R.drawable.ic_placeholder
                        Category(iconRes = icon, name = dto.name)
                    }
                }
                .onFailure { e ->
                    if (BuildConfig.DEBUG) Log.e(TAG, "loadTop failed", e)
                    _top.value = emptyList()
                }
        }
    }

    fun loadSubs(categoryName: String) {
        viewModelScope.launch {
            runCatching {
                api.getSubCategories(categoryName)              // List<SubCategoryDto>
            }.onSuccess { remote: List<SubCategoryDto> ->
                _subs.value = remote.map { it.toUi() }    // → List<Category(iconRes:Int, name:String)
            }.onFailure { e ->
                if (BuildConfig.DEBUG) Log.e(TAG, "loadSubs failed: $categoryName", e)
                _subs.value = emptyList()   //실패 시 빈 리스트
            }
        }
    }

    // HomeScreen에서 호출: 키워드로 상세 이동 대상 조회
    suspend fun search(keyword: String): SearchResultDto? {
        val q = keyword.trim()
        if (q.isBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                val res = detailApi.search(q)
                if (res.isSuccessful) res.body() else null
            } catch (e: Exception) {
                null
            }
        }
    }
}

/** 서버 DTO를 화면 모델로 변환: ".../pet_water.png" -> R.drawable.ic_pet_water */
private fun SubCategoryDto.toUi(): Category {
    val key = imageUrl.substringAfterLast('/').substringBeforeLast('.') // "pet_water"
    val iconId = SubCategoryIcons.byKey[key] ?: R.drawable.ic_placeholder
    return Category(iconRes = iconId, name = name, key = key)
}

