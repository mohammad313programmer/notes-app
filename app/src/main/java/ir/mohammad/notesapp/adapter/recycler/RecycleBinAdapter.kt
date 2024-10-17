package ir.mohammad.notesapp.adapter.recycler

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ir.mohammad.notesapp.R
import ir.mohammad.notesapp.data.local.db.DBHelper
import ir.mohammad.notesapp.data.local.db.dao.NotesDao
import ir.mohammad.notesapp.data.model.RecyclerNotesModel
import ir.mohammad.notesapp.databinding.ListItemRecycleBinBinding

class RecycleBinAdapter(
    private val context: Context,
    private val dao: NotesDao
) : RecyclerView.Adapter<RecycleBinAdapter.RecycleViewHolder>() {

    private val allData = dao.getNotesForRecycler(DBHelper.TRUE_STATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder =
        RecycleViewHolder(
            ListItemRecycleBinBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )

    override fun getItemCount() = allData.size

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        holder.setData(allData[position])
    }

    inner class RecycleViewHolder(
        private val binding: ListItemRecycleBinBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(data: RecyclerNotesModel) {

            binding.txtTitleNotes.text = data.title

            binding.imgDeleteNotes.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("حذف دائمی یادداشت")
                    .setMessage(
                        "یادداشت رو برا همیشه حذف کنم؟"
                    )
                    .setIcon(R.drawable.ic_delete)
                    .setNegativeButton("بله") { _, _ ->
                        val result = dao.deleteNotes(data.id)
                        if (result) {
                            showText("یادداشت بطور کامل حذف شد")
                            allData.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        } else {
                            showText("نتونستم بطور کامل حذفش کنم):")
                        }
                    }
                    .setNeutralButton("خیر") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }

            binding.imgRestoreNotes.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("بازگردانی یادداشت")
                    .setMessage(
                        "یادداشت رو بازگردانی کنم ؟"
                    )
                    .setIcon(R.drawable.ic_restore)
                    .setNegativeButton("بله") { _, _ ->
                        val result = dao.editNotes(data.id, DBHelper.FALSE_STATE)
                        if (result) {
                            showText("بازگردانی شد")
                            allData.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        } else {
                            showText("نتونستم بازگردانی کنم):")
                        }
                    }
                    .setNeutralButton("خیر") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }

        }

    }

    private fun showText(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}