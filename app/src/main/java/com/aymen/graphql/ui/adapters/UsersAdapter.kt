package com.aymen.graphql.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aymen.graphql.R
import com.aymen.graphql.UsersListQuery

/**
 * show list of users in recycler view
 * @author Aymen Masmoudi
 * */
@SuppressLint("NotifyDataSetChanged")
class UsersAdapter(listener: ClickListener) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var users: ArrayList<UsersListQuery.User>

    private lateinit var context: Context

    private val listener: ClickListener

    init {
        this.users = ArrayList()
        this.listener = listener
    }

    //set users list and notify adapter to refresh
    fun setUsers(users: ArrayList<UsersListQuery.User>){
        this.users = ArrayList(users)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvRocket: TextView = view.findViewById(R.id.tv_rocket)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvTwitter: TextView = view.findViewById(R.id.tv_twitter)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.items_users, viewGroup, false)
        context = viewGroup.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        holder.tvName.text = String.format(context.resources.getString(R.string.name), user.name)
        holder.tvRocket.text = String.format(context.resources.getString(R.string.rocket), user.rocket)
        holder.tvTime.text = String.format(context.resources.getString(R.string.time), user.timestamp.toString())
        holder.tvTwitter.text = String.format(context.resources.getString(R.string.twitter), user.twitter)

        holder.itemView.setOnClickListener { listener.onItemClickListener(position) }

    }

    override fun getItemCount() = users.size

    //detect item click
    interface ClickListener {
        fun onItemClickListener(position: Int)
    }

}