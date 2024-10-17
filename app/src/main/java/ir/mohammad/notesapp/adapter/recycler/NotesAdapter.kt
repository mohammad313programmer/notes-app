package ir.mohammad.notesapp.adapter.recycler

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ir.mohammad.notesapp.R
import ir.mohammad.notesapp.data.local.db.DBHelper
import ir.mohammad.notesapp.data.local.db.dao.NotesDao
import ir.mohammad.notesapp.data.model.RecyclerNotesModel
import ir.mohammad.notesapp.databinding.ListItemNotesBinding
import ir.mohammad.notesapp.ui.AddNotesActivity

// ایجاد کاستوم آداپتر برای ریسایکلر ویو
// این آداپتر به عنوان پل ارتباطی بین داده های یادداشت ها  که در یک پایگاه داده ذخیره شده اند
// و نمایش آنها در یک ریسایکلر ویو عمل میکند
class NotesAdapter(
    // کانتکس به آداپتر اجازه میدهد تا به منابع و سرویس های سیستم دسترسی داشته باشد
    private val context: Context,
    private val dao: NotesDao
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    // یک لیست که داده های هر یادداشت را در خود نگه میدارد
    private var allData: ArrayList<RecyclerNotesModel>

    init {
        // در اینجا متغیری که در بالا تعریف کردیم از داده ها پر میشود و در خود ذخیره میکند
        // به عبارتی آیدی ها و تایتل های تمام یادداشت هایی که فالز استیت هستند در این متغیر ذخیره میشود
        allData = dao.getNotesForRecycler(DBHelper.FALSE_STATE)
    }

    // این فانکشن برای ایجاد یک ویوهولدر جدید فراخوانی میشود
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(
            ListItemNotesBinding.inflate(LayoutInflater.from(context), parent, false)
        )

    // این متد برای اتصال داده ها به یک ویوهولدر موجود فراخوانی میشود
    override fun getItemCount() = allData.size

    // این متد تعداد کل آیتم هایی که باید در لیست نمایش داده شوند را برمیگرداند
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.setData(allData[position])
    }

    // این ویوهولدر یک نمونه از ویو را نگه میدارد و اطلاعات مربوط به آن ویو را ذخیره میکند
    // کلاس داخلی است و به پارامتر های کلاس نوتس آداپتر دسترسی دارد
    inner class NotesViewHolder(
        // یک ویو در ورودی این کلاس میگیریم تا وقتی خواستیم از ویوهولدر ارث بری کنیم بتوانیم به آن پاس دهیم
        private val binding: ListItemNotesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // داده های یک یادداشت را دریافت کرده و آن را به عناصر ویو در ویوهولدر متصل میکند
        fun setData(data: RecyclerNotesModel) {
            binding.txtTitleNotes.text = data.title

            binding.imgDeleteNotes.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("انتقال به سطل زباله")
                    .setMessage(
                        "مطمئنی میخوای بفرستیش سطل زباله؟" +
                                " بعدا میتونی برش گردونی سر جاش:)"
                    )
                    .setIcon(R.drawable.ic_delete_empty)
                    .setNegativeButton("بنداز سطل آشغال") { _, _ ->
                        val result = dao.editNotes(data.id, DBHelper.TRUE_STATE)
                        if (result) {
                            showText("فرستادم سطل زباله(:")
                            allData.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        } else {
                            showText("نتونستم بفرستمش سطل آشغال):")
                        }
                    }
                    .setNeutralButton("ننداز لازمش دارم") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }

            binding.root.setOnClickListener {
                val intent = Intent(context, AddNotesActivity::class.java)
                intent.putExtra("notesId", data.id)
                context.startActivity(intent)
            }
        }
    }

    // برای بروزرسانی داده های آداپتر استفاده میشود
    // هنگامی که داده های جدیدی از پایگاه داده دریافت شود این متد فراخوانی میشود
    // تا لیست نمایش داده شده در ریسایکلرویو به روز شود
    @SuppressLint("NotifyDataSetChanged")
    fun changeData(data: ArrayList<RecyclerNotesModel>) {
        allData = data
        notifyDataSetChanged()
    }

    private fun showText(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}