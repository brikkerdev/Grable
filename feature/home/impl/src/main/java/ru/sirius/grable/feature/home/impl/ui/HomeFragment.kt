package ru.sirius.grable.feature.home.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import ru.sirius.grable.feature.home.impl.databinding.FragmentMainScreenBinding
import ru.sirius.grable.core.navigation.api.NavigationRouter
import ru.sirius.grable.core.navigation.api.Screens

class HomeFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val navigationRouter: NavigationRouter by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Повторение изученных слов
        binding.reviewContainer.setOnClickListener {
            navigationRouter.navigateToScreen(Screens.LEARN)
        }
        binding.buttonRepeat.setOnClickListener {
            navigationRouter.navigateToScreen(Screens.LEARN)
        }

        // Выбор подборки для изучения
        binding.collectionContainer.setOnClickListener {
            navigationRouter.navigateToScreen(Screens.SELECT_PLAYLIST)
        }
        binding.buttonCollection.setOnClickListener {
            navigationRouter.navigateToScreen(Screens.SELECT_PLAYLIST)
        }

        // Добавить новое слово
        binding.addWordContainer.setOnClickListener {
            navigationRouter.navigateToScreen(Screens.ADD_WORD)
        }
        binding.buttonAddWord.setOnClickListener {
            navigationRouter.navigateToScreen(Screens.ADD_WORD)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
