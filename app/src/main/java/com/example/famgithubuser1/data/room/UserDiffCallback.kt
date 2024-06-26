package com.example.famgithubuser1.data.room

import androidx.recyclerview.widget.DiffUtil

class UserDiffCallback(
    private val oldNoteList: List<UserLocal>,
    private val newNoteList: List<UserLocal>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldNoteList.size
    override fun getNewListSize(): Int = newNoteList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition].id == newNoteList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldNoteList[oldItemPosition]
        val newNote = newNoteList[newItemPosition]
        return oldNote.id == newNote.id && oldNote.isFavorite == newNote.isFavorite
    }
}