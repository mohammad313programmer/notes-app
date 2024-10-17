package ir.mohammad.notesapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.mohammad.notesapp.R
import ir.mohammad.notesapp.adapter.recycler.NotesAdapter
import ir.mohammad.notesapp.data.local.db.DBHelper
import ir.mohammad.notesapp.data.local.db.dao.NotesDao
import ir.mohammad.notesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dao: NotesDao
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.Ferozeh200)

        initRecycler()

        binding.imgAddNotes.setOnClickListener {
            val intent = Intent(this, AddNotesActivity::class.java)
            intent.putExtra("newNotes", true)
            startActivity(intent)
        }

        binding.txtRecycleBin.setOnClickListener {
            val intent = Intent(this, RecycleBinActivity::class.java)
            startActivity(intent)
        }
    }

    // متد آن استارت هر بار که اکتیویتی شما در حال نمایش به کاربر است، اجرا می‌شود
    // از این متد برای بازیابی آخرین داده‌ها از دیتابیس و نمایش آن‌ها در ریسایکلرویو استفاده شده است
    // این تضمین می‌کند که هر بار که کاربر به این اکتیویتی بازمی‌گردد
    // لیست یادداشت‌ها با داده‌های به‌روز شده نمایش داده شود
    override fun onStart() {
        super.onStart()
        val data = dao.getNotesForRecycler(DBHelper.FALSE_STATE)
        adapter.changeData(data)
    }

    // پیاده سازی ریسایکلرویو
    private fun initRecycler() {
        // یک شیء از کلاس نوتس دی ای او ساخته می‌شود
        // و این شیء برای دسترسی به داده‌های مربوط به یادداشت‌ها از دیتابیس استفاده می‌شود
        // این دی ای او مسئول اجرای عملیات کراد (ایجاد، خواندن، بروزرسانی، حذف) بر روی داده‌های یادداشت است
        dao = NotesDao(DBHelper(this))

        // یک نمونه از نوتس آداپتر ساخته می‌شود
        // نوتس آداپتر همان کلاس آداپتر مربوط به ریسایکلرویو است
        // که وظیفه مدیریت و نمایش داده‌ها را در لیست دارد
        adapter = NotesAdapter(this, dao)

        // تعیین نحوه نمایش آیتم ها در لیست
        binding.recyclerNotes.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        //در اینجا آداپتر که در خط دوم ساخته شده بود، به ریسایکلرویو متصل می‌شود
        // این باعث می‌شود ریسایکلرویو بتواند داده‌های خود را از آداپتر بگیرد و آن‌ها را در لیست نمایش دهد
        binding.recyclerNotes.adapter = adapter
    }

}