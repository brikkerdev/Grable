package ru.sirius.grable.feature.settings.impl.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ru.sirius.feature.settings.impl.R

class SettingsAdapter(
    private val clickListener: ClickListener,
) :
    ListAdapter<SettingItem, RecyclerView.ViewHolder>(Calculator()) {
    interface ClickListener {
        fun onClickListener(item: SettingItem)
        fun onChangeListener(item: SettingItem, value: Boolean)
    }
    companion object {
        private const val TYPE_SECTION_TITLE = 0
        private const val TYPE_BASE_SETTING = 1
        private const val TYPE_SWITCH_SETTING = 2
        private const val TYPE_APP_VERSION = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SettingItem.SectionTitle -> TYPE_SECTION_TITLE
            is SettingItem.BaseSetting -> TYPE_BASE_SETTING
            is SettingItem.SwitchSetting -> TYPE_SWITCH_SETTING
            is SettingItem.AppVersion -> TYPE_APP_VERSION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION_TITLE -> SectionTitleViewHolder(
                inflater.inflate(R.layout.item_title, parent, false)
            )
            TYPE_BASE_SETTING -> BaseSettingViewHolder(
                clickListener,
                inflater.inflate(R.layout.item_base_setting, parent, false)
            )
            TYPE_SWITCH_SETTING -> SwitchSettingViewHolder(
                clickListener,
                inflater.inflate(R.layout.item_base_switch, parent, false)
            )
            TYPE_APP_VERSION -> AppVersionViewHolder(
                inflater.inflate(R.layout.item_app_version, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SettingItem.SectionTitle -> (holder as SectionTitleViewHolder).bind(item)
            is SettingItem.BaseSetting -> (holder as BaseSettingViewHolder).bind(item)
            is SettingItem.SwitchSetting -> (holder as SwitchSettingViewHolder).bind(item)
            is SettingItem.AppVersion -> (holder as AppVersionViewHolder).bind(item)
        }
    }

    // ViewHolder для заголовка раздела
    class SectionTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTitle: TextView = itemView.findViewById(R.id.tvSectionTitle)
        fun bind(item: SettingItem.SectionTitle) {
            sectionTitle.text = itemView.context.getString(item.title)
            sectionTitle.visibility = View.VISIBLE
        }
    }

    // ViewHolder для базовой настройки
    class BaseSettingViewHolder(
        private val clickListener: ClickListener,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val baseSettingLayout: ConstraintLayout = itemView.findViewById(R.id.baseSettingLayout)
        private val settingTitle: TextView = itemView.findViewById(R.id.tvSettingTitle)
        private val settingValue: TextView = itemView.findViewById(R.id.tvSettingValue)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)

        fun bind(item: SettingItem.BaseSetting) {
            cardView.visibility = View.VISIBLE
            baseSettingLayout.visibility = View.VISIBLE
            settingTitle.text = itemView.context.getString(item.title)
            settingValue.text = item.value

            baseSettingLayout.setOnClickListener {
                clickListener.onClickListener(item)
            }
        }
    }

    // ViewHolder для настройки с переключателем
    class SwitchSettingViewHolder(
        private val clickListener: ClickListener,itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val switchSettingLayout: ConstraintLayout = itemView.findViewById(R.id.switchSettingLayout)
        private val switchTitle: TextView = itemView.findViewById(R.id.tvSwitchTitle)
        private val switchSubtitle: TextView = itemView.findViewById(R.id.tvSwitchSubtitle)
        private val switchSetting: Switch = itemView.findViewById(R.id.switchSetting)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)

        fun bind(item: SettingItem.SwitchSetting) {
            cardView.visibility = View.VISIBLE
            switchSettingLayout.visibility = View.VISIBLE
            switchTitle.text = itemView.context.getString(item.title)
            switchSubtitle.text = item.subtitle
            switchSetting.isChecked = item.isChecked

            switchSetting.setOnCheckedChangeListener { _, isChecked ->
                clickListener.onChangeListener(item, isChecked)
            }
        }
    }

    // ViewHolder для версии приложения
    class AppVersionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appVersion: TextView = itemView.findViewById(R.id.tvAppVersion)

        fun bind(item: SettingItem.AppVersion) {
            appVersion.text = item.version
        }
    }

    private class Calculator : DiffUtil.ItemCallback<SettingItem>() {
        override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
}