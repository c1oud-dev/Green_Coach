package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.detail.SubCategoryDetailRepository
import com.application.frontend.model.SubCategoryDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubCategoryDetailViewModel @Inject constructor(
    private val repo: SubCategoryDetailRepository
) : ViewModel() {
    private val _detail = MutableStateFlow<SubCategoryDetail?>(null)
    val detail: StateFlow<SubCategoryDetail?> = _detail

    fun load(key: String) {
        viewModelScope.launch { _detail.value = repo.getDetail(key) }
    }
}