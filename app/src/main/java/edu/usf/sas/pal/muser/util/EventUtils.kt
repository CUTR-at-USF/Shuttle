package edu.usf.sas.pal.muser.util

import android.content.Context
import android.util.Log
import com.simplecity.amp_library.model.Song
import com.simplecity.amp_library.utils.MusicServiceConnectionUtils
import edu.usf.sas.pal.muser.model.*

object EventUtils {
    /**
     * Function to populate the PlayerEvent data class.
     * @param song - The song for which the event occurred.
     * @param capturedEvent - The event that was captured.
     * @param context - The context of the Fragment.
     * @return PlayerEvent object
     */
    @JvmStatic
    fun newPlayerEvent(song: Song?, capturedEvent: PlayerEventType?, context: Context?): PlayerEvent {
        val currentTimeMS = System.currentTimeMillis()
        val nanoTime = System.nanoTime()
        val songData = SongData(song!!, context!!)
        val seekPositionMs = position
        return PlayerEvent(capturedEvent!!, currentTimeMS, nanoTime, seekPositionMs,
                songData)
    }

    /**
     * Function to populate the UiAction data class
     * @param song - The song for which the action was performed.
     * @param capturedUiAction - The action that was captured
     * @param context - The context of the fragment.
     * @return UiAction object
     */
    @JvmStatic
    fun newUiEvent(song: Song, capturedUiAction: UiEventType, context: Context): UiEvent {
        val currentTimeMS = System.currentTimeMillis()
        val nanoTime = System.nanoTime()
        val songData = SongData(song, context)
        val seekPositionMs = position
        return UiEvent(uiEventType = capturedUiAction, currentTimeMs = currentTimeMS,
                nanoTime = nanoTime, seekPositionMs = seekPositionMs, song = songData)
    }

    /**
     * function to get the seek position of the track.
     * @return current seek position. 0 if there's an error
     */
    val position: Long
        get() {
            if (MusicServiceConnectionUtils.serviceBinder != null &&
                    MusicServiceConnectionUtils.serviceBinder.service != null) {
                try {
                    return MusicServiceConnectionUtils.serviceBinder.service.seekPosition
                } catch (e: Exception) {
                    Log.e("EventUtils", "getPosition() returned error: $e")
                }
            }
            return 0
        }
}