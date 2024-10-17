package ir.mohammad.notesapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.mohammad.notesapp.R
import ir.mohammad.notesapp.adapter.recycler.RecycleBinAdapter
import ir.mohammad.notesapp.data.local.db.DBHelper
import ir.mohammad.notesapp.data.local.db.dao.NotesDao
import ir.mohammad.notesapp.databinding.ActivityRecycleBinBinding

class RecycleBinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecycleBinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.Ferozeh200)

        binding.imgBack2.setOnClickListener { finish() }

        initRecycler()

    }

    private fun initRecycler() {
       val dao = NotesDao(DBHelper(this))
       val adapter = RecycleBinAdapter(this, dao)

        binding.recyclerNotes.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerNotes.adapter = adapter
    }

}