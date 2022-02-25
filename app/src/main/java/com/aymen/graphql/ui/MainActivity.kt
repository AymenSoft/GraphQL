package com.aymen.graphql.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.aymen.graphql.UsersListQuery
import com.aymen.graphql.apollo.ApolloInstance
import com.aymen.graphql.ui.adapters.UsersAdapter
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.exception.ApolloException
import com.aymen.graphql.R
import com.aymen.graphql.databinding.ActivityMainBinding

/**
 * import users list from server
 * show users list in recycler view
 * add/update/refresh users list
 * @author Aymen Masmoudi
 * */
@SuppressLint("NotifyDataSetChanged")
class MainActivity : AppCompatActivity(), UsersAdapter.ClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var client: ApolloClient

    private lateinit var arrayList: ArrayList<UsersListQuery.User>
    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = ApolloInstance().get()

        adapter = UsersAdapter(this)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvData.layoutManager = mLayoutManager
        binding.rvData.adapter = adapter

        binding.btnAdd.setOnClickListener {
            val add = Intent(this, UserTreatment::class.java)
            add.action = "add"
            activityResult.launch(add)
        }

        getUsersList()

    }

    //get users list
    private fun getUsersList() {
        lifecycleScope.launchWhenResumed {
            val response = try {
                client.query(UsersListQuery(10)).execute()
            }catch (e : ApolloException){
                binding.tvLoading.text = resources.getString(R.string.protocol_error)
                binding.tvLoading.visibility = View.VISIBLE
                return@launchWhenResumed
            }
            val users = response.data?.users
            if (users == null || response.hasErrors()) {
                binding.tvLoading.text = response.errors?.get(0)?.message
                binding.tvLoading.visibility = View.VISIBLE
                return@launchWhenResumed
            }else {
                arrayList = ArrayList(users)
                adapter.setUsers(arrayList)
                if (arrayList.isEmpty()) {
                    binding.tvLoading.visibility = View.VISIBLE
                    binding.tvLoading.text = resources.getString(R.string.no_data)
                } else {
                    binding.tvLoading.visibility = View.GONE
                }
            }
        }
    }

    //detect item click from recycler view
    override fun onItemClickListener(position: Int) {
        val update = Intent(this, UserTreatment::class.java)
        update.action = "update"
        update.putExtra("id", arrayList[position].id.toString())
        activityResult.launch(update)
    }

    //refresh users list after successful treatment
    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getUsersList()
            }
        }

}