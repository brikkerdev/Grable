package ru.sirius.grable.feature.home.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import ru.sirius.grable.feature.home.impl.databinding.FragmentMainScreenBinding
import ru.sirius.grable.navigation.api.NavigationRouter

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

        binding.buttonRepeat.setOnClickListener {
            // Navigate to LearnFragment using qualifier from API
            navigationRouter.navigateToScreenByQualifier(
                ru.sirius.grable.feature.learn.api.Constants.LEARN_SCREEN
            )
        }

        binding.buttonCollection.setOnClickListener {
            // TODO: Replace with proper qualifier once LearnPlaylistFragment is modularized
            // For now, use LearnFragment qualifier as placeholder
            navigationRouter.navigateToScreenByQualifier(
                ru.sirius.grable.feature.learn.api.Constants.LEARN_SCREEN
            )
        }

        binding.buttonCategories.setOnClickListener {
            // TODO: Replace with proper qualifier once SelectPlaylistFragment is modularized
            // For now, use reflection to create fragment and navigate via router
            navigateToFragmentByReflection("ru.sirius.grable.playlist.ui.SelectPlaylistFragment")
        }
    }

    private fun navigateToFragmentByReflection(className: String) {
        try {
            val fragmentClass = Class.forName(className)
            val fragment = fragmentClass.newInstance() as? Fragment
            fragment?.let {
                navigationRouter.navigateToFragment(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
