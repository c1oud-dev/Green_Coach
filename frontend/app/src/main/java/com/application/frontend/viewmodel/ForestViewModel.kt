package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.co2.Co2Repository
import com.application.frontend.model.Co2Snapshot
import com.application.frontend.model.ForestStage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForestUiState(
    val stage: ForestStage = ForestStage.SEED,
    val progressPercent: Int = 0,
    val shots: Int = 0,
    val world: Co2Snapshot? = null,
    val korea: Co2Snapshot? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val helpDialog: Boolean = false
)

@HiltViewModel
class ForestViewModel @Inject constructor (
    private val repo: Co2Repository,
    private val io: CoroutineDispatcher
) : ViewModel() {

    private val _ui = MutableStateFlow(ForestUiState())
    val ui: StateFlow<ForestUiState> = _ui.asStateFlow()

    fun init(shotsFromScan: Int) {
        // 성장 단계/프로그레스 계산
        val stage = ForestStage.fromShots(shotsFromScan)
        val percent = (shotsFromScan.coerceAtMost(31) * 100) / 31 // 31회 ≈ 100% 기준
        _ui.value = _ui.value.copy(stage = stage, progressPercent = percent, shots = shotsFromScan)

        // CO2 데이터 불러오기
        viewModelScope.launch(io) {
            try {
                val world = repo.world()
                val korea = repo.korea()
                _ui.value = _ui.value.copy(world = world, korea = korea, isLoading = false)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun toggleHelp(show: Boolean) { _ui.value = _ui.value.copy(helpDialog = show) }
}
