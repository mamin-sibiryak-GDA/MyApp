package com.example.myapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MyInterface {
    private lateinit var imageMovie: ImageView
    private var currentPosInMovieList: Int = -1
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var moviesAdapter: MyAdapter
    private var movieList: ArrayList<Movie> = arrayListOf(
        Movie(
            "Зелёный_слоник",
            "Триллер",
            "Светлана Баскова",
            "Supernova",
            R.drawable.green_elephant_poster.toString()
        ),
        Movie(
            "Пять_бутылок_водки",
            "Драма",
            "Светлана Баскова",
            "STYK Production",
            R.drawable.five_bottles_of_vodka_poster.toString()
        ),
        Movie(
            "Розовые_фламинго",
            "Комедия",
            "Джон Уотерс",
            "Dreamland",
            R.drawable.pink_flamingos_poster.toString()
        ),
        Movie(
            "Дом_1000_трупов",
            "Ужасы",
            "Роб Зомби",
            "Lionsgate",
            R.drawable.house_of_1000_corpses_poster.toString()
        ),
        Movie(
            "Клоуны_убийцы_из_космоса",
            "Научная фантастика",
            "Стивен Чиодо",
            "Chiodo Bros.",
            R.drawable.killer_klowns_from_outer_space_poster.toString()
        )
    )

    var dbHelper: MoviesDbHelper? = null // объект класса MoviesDbHelper
    var db: SQLiteDatabase? = null //объект для работы с БД

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        dbHelper = MoviesDbHelper(this) //создаем объект класса MoviesDbHelper
        if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            movieList = savedInstanceState.getSerializable("movies") as ArrayList<Movie>
            Toast.makeText(this, "From saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "From create", Toast.LENGTH_SHORT).show()
            if (dbHelper!!.isEmpty()) { //если БД пустая
                println("DB is emty")
                dbHelper!!.addArrayToDB(movieList) //заносим в нее наш массив
                dbHelper!!.printDB() //и выводим в консоль для проверки
            } else { //иначе, если в БД есть записи
                println("DB has records")
                dbHelper!!.printDB() //выводим записи в консоль для проверки
                movieList = dbHelper!!.getMoviesArray() //и выводим записи в наш массив
            }
        }
        moviesAdapter = MyAdapter(movieList).also { it.myInterface = this }
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = moviesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> onAboutClick(item)
            R.id.addNew -> onAddNewClick(item)
            else -> super.onOptionsItemSelected(item)
        }
    }

    //наш метод для показа диалогового окна с информацией
    private fun onAboutClick(item: MenuItem): Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(item.title)
        //ищем id строкового ресурса с именем about_content – в нем текст для диалогового окна
        val strId = resources.getIdentifier("about_content", "string", packageName)
        var strValue = "" //объявляем пустую строку
        if (strId != 0) strValue = getString(strId) //получаем строку с нужным id
        builder.setMessage(strValue)
        builder.setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
        builder.show()
        return true
    }

    private fun onAddNewClick(item: MenuItem): Boolean {
        val newAct = Intent(applicationContext, InputActivity::class.java)
        secondActivityWithResult.launch(newAct) //запускаем новое активити и ждем от него данные
        return true
    }

    private val secondActivityWithResult = //переменная-объект класса ActivityResultLauncher,
        //ей присваиваем результат вызова метода registerForActivityResult
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //внутри метода смотрим результат работы запущенного активити – если закрыти с кодом RESULT_OK
            if (result.resultCode == Activity.RESULT_OK) { //то берем объект из его данных
                val newMovie = result.data?.getSerializableExtra("newItem") as Movie //как фильм
                movieList.add(0, newMovie) //добавляем в наш список
                moviesAdapter.notifyDataSetChanged() //и уведомляем адаптер об изменениях
                dbHelper!!.addMovie(newMovie)
            }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show() //сообщение для отслеживания
        outState.putSerializable("movies", movieList) //помещаем наш основной массив в хранилище
        //и даем ему метку movies, по ней потом его и найдем
        super.onSaveInstanceState(outState)
    }

    override fun callback(image: ImageView, pos: Int) { //реализация метода
        //запрашиваем разрешение на доступ к чтению файлов из хранилища
        val permission: String = Manifest.permission.READ_EXTERNAL_STORAGE
        val grant = ContextCompat.checkSelfPermission(applicationContext, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = permission
            ActivityCompat.requestPermissions(this, permission_list, 1)
        }
        currentPosInMovieList = pos //позиция текущего элемента списка, у которого меняем картинку
        println("image = $image") //отладочный вывод (будет в разделе Run внизу IDE)
        println("pos = $currentPosInMovieList")
        imageMovie = image //текущий объект для изменяемой картинки
        val intent = Intent()
            .setType("image/*")
            .setAction(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
        getImg.launch(intent) //запускаем метод для получения новой картинки из ресурсов девайса
    }

    //собственно метод для получения новой картинки из ресурсов девайса
    //запускается метод registerForActivityResult с контрактом на получение данных
    val getImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {//если файл получен
                val uri = result.data?.data //берем его uri
                imageMovie.setImageURI(uri) // устанавливаем его для нашего объекта ImageView
                println("image uri = $uri") //отладочный вывод (будет в разделе Run внизу IDE)
                movieList[currentPosInMovieList].picture =
                    uri.toString()//меняем у текущего объекта картинку на новую
                dbHelper!!.changeImgForMovie(
                    movieList[currentPosInMovieList].name,
                    movieList[currentPosInMovieList].picture
                )
            }
        }

    override fun del(pos: Int) {
        currentPosInMovieList = pos
        val name = movieList[pos].name
        movieList.removeAt(pos)
        dbHelper!!.delMovie(name)

        moviesAdapter.notifyDataSetChanged()
    }
}
