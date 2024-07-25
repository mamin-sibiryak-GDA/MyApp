package com.example.myapp

import android.app.AlertDialog
import android.net.Uri
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private var movieList: ArrayList<Movie>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>(), View.OnCreateContextMenuListener {
    var myInterface: MyInterface? = null

    class MyViewHolder(view: View, onCreateContextMenuListener: View.OnCreateContextMenuListener) :
        RecyclerView.ViewHolder(view) {
        var movieName: TextView = view.findViewById(R.id.name)
        var movieGenre: TextView = view.findViewById(R.id.genre)
        var movieDirector: TextView = view.findViewById(R.id.director)
        var movieCompany: TextView = view.findViewById(R.id.company)
        var moviePoster: ImageView = view.findViewById(R.id.imageView1)
        fun showMessage(str: String?, view: View) {
            //создаем диалоговое окно, параметр – контекст, который берем у view
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(str) //заголовок диалогового окна
            //создаем переменную для нахождения строкового ресурса
            //ищем в строковых ресурсах строку с именем, равным значению str и берем её идентификатор
            val strId: Int =
                view.context.resources.getIdentifier(str, "string", view.context.packageName)
            var strValue: String? = "" //для получения значения строки из строковых ресурсов
            //если ресурс был найден, т.е. strId!=0, то по найденному идентификатору получаем значение строки
            if (strId != 0) strValue = view.context.getString(strId)
            builder.setMessage(strValue) //задаем содержимое окна
            //создаем в окне кнопку ОК и задаем ее функционал
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                dialog.dismiss() // при нажатии на Ок закрываем диалоговое окно
            }
            val dialog: AlertDialog = builder.create() //создаем диалоговое окно через построитель
            dialog.show() //показываем диалоговое окно
        }

        init {
            itemView.setOnClickListener {
                Toast.makeText(view.context, "pos = " + adapterPosition, Toast.LENGTH_LONG).show()
                showMessage(movieName.text.toString(), view)
                itemView.setOnCreateContextMenuListener(onCreateContextMenuListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.my_list, parent, false)
        return MyViewHolder(itemView, this)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = movieList[position]
        holder.itemView.tag = position
        holder.movieName.text = movie.name
        holder.movieGenre.text = movie.genre
        holder.movieDirector.text = movie.director
        holder.movieCompany.text = movie.company
        try { //в секции try … catch обрабатываем вывод картинки в объект ImageView
            holder.moviePoster.setImageResource(movie.picture.toInt())//тут из базового списка
        } catch (e: ClassCastException) {//тут и в следующем если картинка менялась через контекстное меню
            holder.moviePoster.setImageURI(Uri.parse(movie.picture))
        } catch (e: NumberFormatException) {
            holder.moviePoster.setImageURI(Uri.parse(movie.picture))
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        view: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val edit = menu.add(Menu.NONE, 1, 1, "Изменить фото") //создаем пункт меню Изменить фото
        edit.setOnMenuItemClickListener { //и делаем обработчик нажатия
            //получаем ссылку на картинку как объект с компоновки (0 – это лейаут с текстовыми полями,
            //1 – это картинка, см. разметку интерфейса элемента списка)
            var pic = (view as LinearLayout).getChildAt(1) as ImageView
            val pos = view.tag as Int //получаем номер объекта в списке через поле tag
            myInterface?.callback(pic, pos) //вызываем метод для обработки изменения изображения
            false //по умолчанию возвращаем false
        }
        val delete =
            menu.add(Menu.NONE, 2, 2, "Удалить") // еще один пункт меню, его не обрабатываем
        delete.setOnMenuItemClickListener {
            val pos = view.tag as Int

            myInterface?.del(pos)
            notifyDataSetChanged()
            false
        }
    }
}
