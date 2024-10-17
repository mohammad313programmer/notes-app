package ir.mohammad.notesapp.ui

import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.mohammad.notesapp.R
import ir.mohammad.notesapp.data.local.db.DBHelper
import ir.mohammad.notesapp.data.local.db.dao.NotesDao
import ir.mohammad.notesapp.data.model.DBNotesModel
import ir.mohammad.notesapp.databinding.ActivityAddNotesBinding
import ir.mohammad.notesapp.utils.PersianDate

// کلاس اد نوتس اکتیویتی مسئولیت ایجاد و ویرایش یادداشت‌ها را در اپلیکیشن شما بر عهده دارد
// این اکتیویتی به کاربر اجازه می‌دهد تا یادداشتی جدید ایجاد کند یا یک یادداشت موجود را ویرایش کند
class AddNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.Ferozeh200)

// این دو خط مقادیر ارسال شده به این اکتیویتی را از اینتنت دریافت می‌کنند
        val type = intent.getBooleanExtra("newNotes", false)
        val id = intent.getIntExtra("notesId", 0)

        val dao = NotesDao(DBHelper(this))

        if (type) {
            binding.txtDate.text = getDate()
        } else {
            val notes = dao.getNotesById(id)
            val edit = Editable.Factory()
            binding.edtTitleNotes.text = edit.newEditable(notes.title)
            binding.edtDetailNotes.text = edit.newEditable(notes.detail)
            binding.txtDate.text = notes.date
        }

        binding.imgSave.setOnClickListener {
            val title = binding.edtTitleNotes.text.toString()
            val detail = binding.edtDetailNotes.text.toString()
            if (title.isNotEmpty()) {
                val notes = DBNotesModel(0, title, detail, DBHelper.FALSE_STATE, getDate())
                val result = if (type)
                    dao.saveNotes(notes)
                else
                    dao.editNotes(id, notes)

                if (result) {
                    showText("ذخیره شد")
                    finish()
                } else
                    showText("نتونستم ذخیره کنم")
            } else
                showText("عنوان خالیه")
        }
        binding.imgBack.setOnClickListener { finish() }

    }

    private fun getDate(): String {
        // با استفاده از کلاس PersianDate تاریخ شمسی و ساعت کنونی را بدست می آوریم
        val persianDate = PersianDate()
        // این کد تاریخ شمسی فعلی را به رشته تبدیل میکند
        val currentDate = "${persianDate.year} / ${persianDate.month} / ${persianDate.day}"
        // این کد ساعت و دقیقه و ثانیه فعلی را به رشته تبدیل میکند
        val currentTime = "${persianDate.hour} :${persianDate.min}"
        // در اینجا ساعت و تاریخ را به هم چسبانده و return میکنیم
        return "$currentDate    ||    $currentTime"
    }

    private fun showText(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}