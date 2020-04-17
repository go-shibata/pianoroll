package com.example.go.piano_pitch.ui.pitch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.go.piano_pitch.databinding.FragmentPitchBinding
import com.example.go.piano_pitch.di.ViewModelFactory
import com.example.go.piano_pitch.ui.view.note.NoteTextView
import com.example.go.piano_pitch.ui.view.note.NotesListView
import com.example.go.piano_pitch.ui.view.piano.PianoView
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class PitchFragment : Fragment(), PianoView.OnPlayListener {

    @Inject
    lateinit var factory: ViewModelFactory<PitchViewModel>
    private val viewModel: PitchViewModel by viewModels { factory }

    private lateinit var binding: FragmentPitchBinding

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotNull(arguments).let {
            val pitchType = PitchFragmentArgs.fromBundle(it).pitchType
            viewModel.setPitchType(pitchType)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPitchBinding.inflate(inflater, container, false).apply {
            fragment = this@PitchFragment
            viewModel = this@PitchFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            piano.setOnPlayListener(this@PitchFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playedNote.observe(viewLifecycleOwner, Observer {
            val textView = NoteTextView(requireContext(), it.name)
            binding.aNotes.addView(textView)
        })
        viewModel.question.observe(viewLifecycleOwner, Observer {
            it.forEach { list ->
                val listView = NotesListView(requireContext(), list)
                binding.qNotes.addView(listView)
            }

            CoroutineScope(Dispatchers.Default).launch {
                it.forEach { list ->
                    list.forEach { note ->
                        delay(1000)
                        binding.piano.play(note.note)
                    }
                }
            }
        })
    }

    override fun onPlay(note: Int) {
        viewModel.setPlayedNote(note)
    }

    @ExperimentalStdlibApi
    fun onClickButton() {
        when {
            viewModel.isStarted.value == false -> {
                viewModel.fetchQuestion()
            }
            viewModel.isFinish.value == true -> {
                findNavController().navigate(
                    PitchFragmentDirections.actionPitchFragmentToPitchResultFragment(
                        viewModel.results.toTypedArray()
                    )
                )
            }
            else -> {
                binding.run {
                    aNotes.removeAllViews()
                    qNotes.removeAllViews()
                }
                viewModel.fetchQuestion()
            }
        }
    }
}
