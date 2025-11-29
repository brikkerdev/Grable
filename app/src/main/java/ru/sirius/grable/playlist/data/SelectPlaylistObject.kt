package ru.sirius.grable.playlist.data

import ru.sirius.grable.main.Playlist

object SelectPlaylistObject {
    val playlists: List<Playlist> = listOf(
        Playlist("food", "Еда", "Слова о еде"),
        Playlist("medicine", "Медицина", "Медицинские термины"),
        Playlist("animals", "Животные", "Названия животных"),
        Playlist("transport", "Транспорт", "Виды транспорта и связанные слова"),
        Playlist("technology", "Технологии", "Технические термины"),
        Playlist("clothes", "Одежда", "Предметы одежды и аксессуары"),
        Playlist("nature", "Природа", "Слова о природных явлениях и объектах"),
        Playlist("house", "Дом", "Предметы и понятия, связанные с домом"),
        Playlist("school", "Школа", "Учебные принадлежности и термины"),
        Playlist("sports", "Спорт", "Виды спорта и спортивные термины"),
        Playlist("jobs", "Профессии", "Названия профессий")
    )
}