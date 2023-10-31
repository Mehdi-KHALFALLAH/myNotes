import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.DeleteNote
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesEvent
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state : State<NotesState> = _state
    private var recentlyDeletedNote : Note? = null
    fun onEvent (event: NotesEvent) {
        when (event) {
            is NotesEvent.order -> {
               if (state.value.noteOrder ::class == event.noteOrder::class &&
                       state.value.noteOrder.orderType== event.noteOrder.orderType) {
                   return
               }
            }
            is NotesEvent.DeleteNote -> {
             viewModelScope.launch {
                 noteUseCases.deleteNote(event.note)
                 recentlyDeletedNote = event.note
             }
            }
            is NotesEvent.RestoreNote -> {
             viewModelScope.launch {
                 noteUseCases.addNote(recentlyDeletedNote?: return@launch)
                 recentlyDeletedNote=null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy( isOrderSectionVisible = !state.value.isOrderSectionVisible)


            }
        }
    }

}