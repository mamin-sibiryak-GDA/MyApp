package com.example.myapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MoviesDbHelper(context: Context) : //наш класс для работы с БД, наследуется от
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) { //стандартного класса
    companion object { // тут прописываем переменные для БД
        private val DATABASE_NAME = "MOVIES" //имя БД
        private val DATABASE_VERSION = 1 // версия
        val TABLE_NAME = "movies_table" // имя таблицы
        val ID_COL = "id" // переменная для поля id
        val NAME_COl = "movie_name" // переменная для поля movie_name
        val GENRE_COL = "genre" // переменная для поля year
        val DIRECTOR_COL = "director"
        val COMPANY_COL = "company"
        val PICTURE_COL = "picture" // переменная для поля picture
    }

    override fun onCreate(db: SQLiteDatabase) { //метод для создания таблицы через SQL-запрос
        val query = ("CREATE TABLE " + TABLE_NAME + " (" //конструируем запрос через
                + ID_COL + " INTEGER PRIMARY KEY autoincrement, " + //созданные выше
                NAME_COl + " TEXT," + //переменные
                GENRE_COL + " TEXT," +
                DIRECTOR_COL + " TEXT," +
                COMPANY_COL + " TEXT," +
                PICTURE_COL + " TEXT" + ")")
        db.execSQL(query) // выполняем SQL-запрос
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {//метод для обновления БД
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun getCursor(): Cursor? { // метод для получения всех записей таблицы БД в виде курсора
        val db = this.readableDatabase // получаем ссылку на БД только для чтения
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null) //возвращаем курсор в виде
    }

    fun isEmpty(): Boolean { //метод для проверки БД на отсутствие записей
        val cursor = getCursor() //получаем курсор таблицы БД с записями
        return !cursor!!.moveToFirst() //и возвращаем результат перехода к первой записи,
    } //инвертируя его, т.е. если нет записей, cursor!!.moveToFirst() вернет false, отрицание его даст true

    fun printDB() { //метод для печати БД в консоль
        val cursor = getCursor() //получаем курсор БД
        if (!isEmpty()) { //если БД не пустая
            cursor!!.moveToFirst() //переходим к первой записи
            val nameColIndex = cursor.getColumnIndex(NAME_COl) //получаем индексы для колонок
            val genreColIndex = cursor.getColumnIndex(GENRE_COL) //с нужными данными
            val directorColIndex = cursor.getColumnIndex(DIRECTOR_COL)
            val companyColIndex = cursor.getColumnIndex(COMPANY_COL)
            val pictureColIndex = cursor.getColumnIndex(PICTURE_COL)
            do { //цикл по всем записям
                print("${cursor.getString(nameColIndex)} ") //печатаем данные поля с именем
                print("${cursor.getString(genreColIndex)} ") //поля с жанром
                print("${cursor.getString(directorColIndex)} ")
                print("${cursor.getString(companyColIndex)} ")
                println("${cursor.getString(pictureColIndex)} ") //поля с картинкой
            } while (cursor.moveToNext()) //пока есть записи
        } else println("DB is empty") //иначе печатаем, что БД пустая
    }

    fun addArrayToDB(movies: ArrayList<Movie>) { //метод для добавления целого массива в БД
        movies.forEach { //цикл по всем элементам массива
            addMovie(it) //добавляем элемент массива в БД
        }
    }

    fun addMovie(movie: Movie) { // метод для добавления языка в БД
        val values = ContentValues() // объект для создания значений, которые вставим в БД
        values.put(NAME_COl, movie.name) // добавляем значения в виде пары ключ-значение
        values.put(GENRE_COL, movie.genre)
        values.put(DIRECTOR_COL, movie.director)
        values.put(COMPANY_COL, movie.company)
        values.put(PICTURE_COL, movie.picture)
        val db = this.writableDatabase //получаем ссылку для записи в БД
        db.insert(TABLE_NAME, null, values) // вставляем все значения в БД в нашу таблицу
        db.close() // закрываем БД (для записи)
    }

    fun delMovie(name: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, NAME_COl + " = '$name'", null)
    }

    fun changeImgForMovie(name: String, img: String) { // метод для изменения картинки для языка
        val db = this.writableDatabase //получаем ссылку для записи в БД
        val values = ContentValues() // объект для изменения записи
        values.put(PICTURE_COL, img) // вставляем новую картинку
        // и делаем запрос в БД на изменение поля с нужным названием в нашей таблице
        db.update(TABLE_NAME, values, NAME_COl + " = '$name'", null)
        db.close() // закрываем БД (для записи)
    }

    fun getMoviesArray(): ArrayList<Movie> { // метод для получения данных из таблицы в виде массива
        var moviesArray = ArrayList<Movie>() //массив, в который запишем данные
        val cursor = getCursor() //получаем курсор таблицы БД
        if (!isEmpty()) { //если БД не пустая
            cursor!!.moveToFirst() //переходим к первой записи
            val nameColIndex = cursor.getColumnIndex(NAME_COl) //получаем индексы для колонок
            val genreColIndex = cursor.getColumnIndex(GENRE_COL) //с нужными данными
            val directorColIndex = cursor.getColumnIndex(DIRECTOR_COL)
            val companyColIndex = cursor.getColumnIndex(COMPANY_COL)
            val pictureColIndex = cursor.getColumnIndex(PICTURE_COL)
            do { //цикл по всем записям
                val name = cursor.getString(nameColIndex) //получаем данные полей
                val genre = cursor.getString(genreColIndex) //и записываем их в переменные
                val director = cursor.getString(directorColIndex)
                val company = cursor.getString(companyColIndex)
                val picture = cursor.getString(pictureColIndex)
                moviesArray.add(Movie(name, genre, director, company, picture)) //и создаем объект с этими данными
            } while (cursor.moveToNext()) //пока есть записи
        } else println("DB is empty") //иначе пишем, что БД пустая
        return moviesArray //возвращаем созданный массив
    }
}