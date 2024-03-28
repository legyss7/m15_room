package com.hw15.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hw15.database.App
import com.hw15.database.Word
import com.hw15.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() {
            return _binding!!
        }

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClassL: Class<T>): T {
                val wordDao = (binding.root.context.applicationContext as App)
                    .db.wordDao()
                return MainViewModel(wordDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureUIListeners()

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                updateUIState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.allWords.collect { list ->
                updateWordsList(list)
            }
        }

    }

    private fun updateUIState(state: State) {
        when (state) {
            is State.START -> {
                binding.save.isEnabled = false
                binding.textInput.isErrorEnabled = false
                binding.textInputEdit.text?.clear()
            }

            is State.SUCCESS -> {
                binding.save.isEnabled = true
                binding.textInput.isErrorEnabled = false
            }

            is State.ERROR -> {
                binding.save.isEnabled = false
                binding.textInput.error = state.error
            }
        }
    }

    private fun updateWordsList(list: List<Word>) {
        val stringBuilderWord = StringBuilder()
        val stringBuilderCount = StringBuilder()
        list.forEach { word ->
            stringBuilderWord.append("${word.word}\n")
            stringBuilderCount.append("${word.count}\n")
        }
        binding.textWord.text = stringBuilderWord.toString()
        binding.textCount.text = stringBuilderCount.toString()
    }

    private fun configureUIListeners() {
        binding.textInputEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.inputText(text)
        }

        binding.save.setOnClickListener {
            viewModel.onSave(binding.textInputEdit.text.toString())
        }

        binding.clear.setOnClickListener {
            viewModel.onDelete()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}