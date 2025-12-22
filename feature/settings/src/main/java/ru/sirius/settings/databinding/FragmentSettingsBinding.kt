package ru.sirius.settings.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.settings.R

class FragmentSettingsBinding(rootView: View) {
    val root: View = rootView

    val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)

    companion object {
        fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean): FragmentSettingsBinding {
            val rootView = inflater.inflate(R.layout.fragment_settings, container, attachToRoot)
            return FragmentSettingsBinding(rootView)
        }
    }
}