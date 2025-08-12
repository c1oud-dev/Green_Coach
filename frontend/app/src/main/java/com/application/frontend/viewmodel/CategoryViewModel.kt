package com.application.frontend.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.BuildConfig
import com.application.frontend.R
import com.application.frontend.data.CategoryApi
import com.application.frontend.model.Category
import com.application.frontend.model.SubCategoryDto
import com.application.frontend.model.SubCategoryIcons
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CategoryAPI"
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val api: CategoryApi,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // ① 상단에 표시할 메인 카테고리 리스트를 미리 정의
    val topCategories: List<Category> = listOf(
        Category(R.drawable.ic_pet,     "페트병"),
        Category(R.drawable.ic_plastic, "플라스틱 용기"),
        Category(R.drawable.ic_bag,     "비닐류"),
        Category(R.drawable.ic_styro,   "스티로폼"),
        Category(R.drawable.ic_can,     "캔류"),
        Category(R.drawable.ic_glass,   "유리병"),
        Category(R.drawable.ic_paper,   "종이류"),
        Category(R.drawable.ic_milk,    "종이팩"),
        Category(R.drawable.ic_box,     "박스/골판지"),
        Category(R.drawable.ic_cloth,   "옷/섬유류"),
        Category(R.drawable.ic_bulb,    "소형가전"),
        Category(R.drawable.ic_washer,  "대형가전")
    )
    private val _subs = MutableStateFlow<List<Category>>(emptyList())
    val subs: StateFlow<List<Category>> = _subs

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
}

/** 서버 DTO를 화면 모델로 변환: ".../pet_water.png" -> R.drawable.ic_pet_water */
private fun SubCategoryDto.toUi(): Category {
    val key = imageUrl.substringAfterLast('/').substringBeforeLast('.') // "pet_water"
    val iconId = SubCategoryIcons.byKey[key] ?: R.drawable.ic_placeholder
    return Category(iconRes = iconId, name = name)
}

