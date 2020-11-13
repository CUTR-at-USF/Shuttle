package com.simplecity.amp_library.utils.menu.genre

import android.content.Context
import com.simplecity.amp_library.model.Genre
import com.simplecity.amp_library.model.Playlist

interface GenreMenuCallbacks {

    fun createPlaylist(genre: Genre)

    fun addToPlaylist(context: Context, playlist: Playlist, genre: Genre)

    fun addToQueue(genre: Genre)

    fun play(genre: Genre)

    fun playNext(genre: Genre)
}