package com.example.go.piano_pitch.ui.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.go.piano_pitch.databinding.FragmentMenuBinding
import com.example.go.piano_pitch.logic.PitchType
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MenuFragment : Fragment(), MenuEpoxyController.OnClickPitchTypeListener {

    @Inject
    lateinit var epoxyController: MenuEpoxyController

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMenuBinding.inflate(inflater, container, false).apply {
            fragment = this@MenuFragment
            listPitchType.apply {
                setController(epoxyController)
            }.requestModelBuild()
        }
        return binding.root
    }

    fun onClickPianoButton() {
        findNavController().navigate(
            MenuFragmentDirections.actionMenuFragmentToPianoFragment()
        )
    }

    @ExperimentalStdlibApi
    override fun onClick(pitchType: PitchType) {
        findNavController().navigate(
            MenuFragmentDirections.actionMenuFragmentToPitchFragment(pitchType, pitchType.title)
        )
    }
}
