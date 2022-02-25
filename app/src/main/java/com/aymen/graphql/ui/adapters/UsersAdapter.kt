package com.aymen.graphql.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aymen.graphql.R
import com.aymen.graphql.UsersListQuery
import com.aymen.graphql.databinding.ItemsUsersBinding

/**
 * show list of users in recycler view
 * @author Aymen Masmoudi
 * */
@SuppressLint("NotifyDataSetChanged")
class UsersAdapter(listener: ClickListener) : RecyclerView.Adapter<UsersAdapter.UsersHolder>() {

    private lateinit var context: Context
    private var selectedItemPosition: Int
    private var users: ArrayList<UsersListQuery.User>
    private val listener: ClickListener

    init {
        selectedItemPosition = -1
        this.users = ArrayList()
        this.listener = listener
    }

    //set users list and notify adapter to refresh
    fun setUsers(users: ArrayList<UsersListQuery.User>){
        this.users = ArrayList(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersHolder {
        val binding = ItemsUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        this.context = parent.context
        return UsersHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.adapterPosition
            notifyDataSetChanged()
            listener.onItemClickListener(position)
        }
    }

    override fun getItemCount() = users.size

    inner class UsersHolder(private val binding: ItemsUsersBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            val user = users[position]

            binding.tvName.text = String.format(context.resources.getString(R.string.name), user.name)
            binding.tvRocket.text = String.format(context.resources.getString(R.string.rocket), user.rocket)
            binding.tvTime.text = String.format(context.resources.getString(R.string.time), user.timestamp.toString())
            binding.tvTwitter.text = String.format(context.resources.getString(R.string.twitter), user.twitter)

            binding.tvName.setTextColor(context.getColor(
                if (selectedItemPosition == position) R.color.black else android.R.color.darker_gray
            ))

        }
    }

    //detect item click
    interface ClickListener {
        fun onItemClickListener(position: Int)
    }

}