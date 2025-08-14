package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.detail.SubCategoryDetailRepository
import com.application.frontend.model.SubCategoryDetail
import com.application.frontend.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException

@HiltViewModel
class SubCategoryDetailViewModel @Inject constructor(
    private val repo: SubCategoryDetailRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<SubCategoryDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<SubCategoryDetail>> = _uiState.asStateFlow()

    private var lastKey: String? = null

    fun load(key: String) {
        lastKey = key
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val detail = repo.getDetail(key) // 가능성이 SubCategoryDetail?
                _uiState.value = if (detail != null) {
                    UiState.Success(detail)      // non-null만 성공으로
                } else {
                    UiState.Error("상세 데이터를 찾을 수 없어요. 잠시 후 다시 시도해주세요.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(mapError(e))
            }
        }
    }

    fun retry() = lastKey?.let { load(it) }

    private fun mapError(e: Exception): String {
        return when (e) {
            is HttpException -> "서버 오류(${e.code()})가 발생했어요. 잠시 후 다시 시도해주세요."
            is IOException -> "네트워크 연결을 확인하고 다시 시도해주세요."
            else -> "알 수 없는 오류가 발생했어요. 다시 시도해주세요."
        }
    }
}