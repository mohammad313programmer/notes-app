package ir.mohammad.notesapp.data.local.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import ir.mohammad.notesapp.data.model.RecyclerNotesModel
import ir.mohammad.notesapp.data.local.db.DBHelper
import ir.mohammad.notesapp.data.model.DBNotesModel
import java.lang.Exception

// در این کلاس ما چهار عمل مهم را بر روی جدول مورد نظر در دیتابیس انجام میدهیم
// آن چهار عملیات مهم به شرح زیر میباشد
// سیو کردن
// ادیت کردن
// سلکت کردن
// دیلیت کردن
//  برای تعامل با پایگاه داده و انجام عملیات کراد (CRUD) روی یادداشت ها استفاده میشود
//  (C) for Create
// (R) for Read
// (U) for Update
// (D) for Delete
class NotesDao(
    private val db: DBHelper
) {
    private lateinit var cursor: Cursor
    private val contentValues = ContentValues()

    //فانکشن مخصوص سیو کردن یادداشت
    fun saveNotes(notes: DBNotesModel): Boolean {
        val database = db.writableDatabase
        // به وسیله این فانکشن محتوا(یادداشت) را میگیریم و درون متغیر کانتنت ولیوز قرار میدهیم
        setContentValues(notes)
        val result = database.insert(DBHelper.NOTES_TABLE, null, contentValues)
        database.close()
        return result > 0
    }

    // فانکشن مخصوص انتقال یادداشت به سطل زباله (تغییر استیت یادداشت )
    fun editNotes(id: Int, state: String): Boolean {
        val dataBase = db.writableDatabase
        contentValues.clear()
        contentValues.put(DBHelper.NOTES_DELETE_STATE, state)
        val result = dataBase.update(
            DBHelper.NOTES_TABLE,
            contentValues,
            "${DBHelper.NOTES_ID} = ?",
            arrayOf(id.toString())
        )
        dataBase.close()
        return result > 0
    }

    // فانکشن مخصوص ادیت و ویرایش کامل یادداشت مورد نظر
    fun editNotes(id: Int, notes: DBNotesModel): Boolean {
        val database = db.writableDatabase
        setContentValues(notes)
        val result = database.update(
            DBHelper.NOTES_TABLE,
            contentValues,
            "${DBHelper.NOTES_ID} = ?",
            arrayOf(id.toString())
        )
        database.close()
        return result > 0
    }

    // فانکشن مخصوص پر کردن متغیر کانتنت ولیوز
    private fun setContentValues(notes: DBNotesModel) {
        contentValues.clear()
        contentValues.put(DBHelper.NOTES_TITLE, notes.title)
        contentValues.put(DBHelper.NOTES_DETAIL, notes.detail)
        contentValues.put(DBHelper.NOTES_DELETE_STATE, notes.deleteState)
        contentValues.put(DBHelper.NOTES_DATE, notes.date)
    }

    // فانکشن مخصوص به نمایش گذاشتن یادداشت ها در ریسایکلر
    // گاهی یادداشت هایی که دیلیت استیت آنها فالز است نمایش میدهد
    // و گاهی در جای دیکری آن یادداشت هایی را نمایش میدهد که دیلیت استیت آن ترو باشد
    fun getNotesForRecycler(value: String): ArrayList<RecyclerNotesModel> {
        val database = db.readableDatabase
        val query = "SELECT ${DBHelper.NOTES_ID}, ${DBHelper.NOTES_TITLE} " +
                "FROM ${DBHelper.NOTES_TABLE} " +
                "WHERE ${DBHelper.NOTES_DELETE_STATE} = ?"
        // متد راو کوئری کد های اس کیو ال را اجرا میکند
        // که ما در اینجا کدهارا درون متغیر کوئری ذخیره کرده ایم و به این راو کوئری پاس میدهیم
        //و نتیجه ی اجرا شدن یا نشدن کد های اس کیو ال را درون یک متغیر کرسر ذخیره میکنیم
        cursor = database.rawQuery(query, arrayOf(value))
        // فانکشن گت دیتا فور ریسایکلر وظیفه ی استخراج داده ها از کرسر و ساختن یک شی ریسایکلر نوتس مادل را بر عهده دارد
        val data = getDataForRecycler()
        cursor.close()
        database.close()
        return data
    }

    // این فانکشن داده‌های خام گرفته شده از دیتابیس که به شکل یک کرسر است را به یک فرمت
    // قابل استفاده در ریسایکلرویو تبدیل میکند
    // به عبارت دیگر دادهای خام را از دیتابیس میگیرد
    // و آنها را به یک لیست از نوع ریسایکلرنوتس مادل تبدیل میکند
    // که این لیست حاوی اطلاعاتی است که قرار است در هر سطر از ریسایکلر ویو نمایش داده شود
    private fun getDataForRecycler(): ArrayList<RecyclerNotesModel> {
        val data = ArrayList<RecyclerNotesModel>()
        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(getIndex(DBHelper.NOTES_ID))
                    val title = cursor.getString(getIndex(DBHelper.NOTES_TITLE))
                    data.add(RecyclerNotesModel(id, title))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
        }
        return data
    }

    // این فانکشن با دریافت یک آیدی یادداشتی با آن آیدی را از دیتابیس انتخاب کرده
    // و اطلاعات آن را به صورت یک شیئ از نوع دی بی نوتس مادل برمیگرداند
    fun getNotesById(id: Int): DBNotesModel {
        val database = db.readableDatabase
        val query = "SELECT * FROM ${DBHelper.NOTES_TABLE} WHERE ${DBHelper.NOTES_ID} = ?"
        cursor = database.rawQuery(query, arrayOf(id.toString()))
        // گت دیتا وظیفه ی استخراج داده ها از کرسر و ساختن یک شیئ دی بی نوتس مادل را بر عهده دارد
        val data = getData()
        cursor.close()
        database.close()
        return data
    }

    // این فانکشن بررسی میکند که آیا کرسر حاوی داده ای است یا خیر
    // اگر داده ای وجود داشته باشد مقادیر ستون های مختلف را از کرسر استخراج میکند
    // با استفاده از مقادیر استخراج شده یک شیئ از نوع دی بی نوتس مادل ایجاد میکند
    // و در آخر شیئ ایجاد شده را برمیگرداند
    private fun getData(): DBNotesModel {
        val data = DBNotesModel(0, "", "", "", "")
        try {
            if (cursor.moveToFirst()) {
                data.id = cursor.getInt(getIndex(DBHelper.NOTES_ID))
                data.title = cursor.getString(getIndex(DBHelper.NOTES_TITLE))
                data.detail = cursor.getString(getIndex(DBHelper.NOTES_DETAIL))
                data.deleteState = cursor.getString(getIndex(DBHelper.NOTES_DELETE_STATE))
                data.date = cursor.getString(getIndex(DBHelper.NOTES_DATE))
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
        }
        return data
    }

    // این فانکشن  نام ستونی را به عنوان ورودی دریافت می‌کند
    // این نام را به فانکشن گت کالمن ایندکس منتقل میکند
    // فانکشن گت کالمن ایندکس در کرسر جستجو میکند تا ایندکس ستونی با نام مشخص شده را پیدا کند
    // ایندکس پیدا شده به عنوان نتیجه ی فانکشن گت ایندکس برگردانده میشود
    private fun getIndex(name: String) = cursor.getColumnIndex(name)

    // فانکشن مخصوص حذف کامل یادداشت ها از دیتابیس بر اساس آیدی
    fun deleteNotes(id: Int): Boolean {
        val dataBase = db.writableDatabase
        val result = dataBase.delete(
            DBHelper.NOTES_TABLE,
            "${DBHelper.NOTES_ID} = ?",
            arrayOf(id.toString())
        )
        dataBase.close()
        return result > 0
    }

}