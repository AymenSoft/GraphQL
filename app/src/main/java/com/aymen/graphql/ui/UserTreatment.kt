package com.aymen.graphql.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.aymen.graphql.*
import com.aymen.graphql.apollo.ApolloInstance
import com.aymen.graphql.databinding.ActivityUserTreatmentBinding
/**
 * insert/update/delete user
 * @author Aymen Masmoudi
 * */
class UserTreatment : AppCompatActivity() {

    private lateinit var binding: ActivityUserTreatmentBinding

    private lateinit var client: ApolloClient

    private lateinit var activityAction: String
    private lateinit var id: String

    private lateinit var user: UserByIdQuery.Users_by_pk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserTreatmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = ApolloInstance().get()

        //insert/update user depending on activityAction
        binding.btnSave.setOnClickListener{
            when(activityAction){
                "add" -> insertUser()
                "update" -> updateUser()
            }
        }

        binding.btnDelete.setOnClickListener {
            val deleteDialog = AlertDialog.Builder(this)
            deleteDialog.setTitle(R.string.app_name)
            deleteDialog.setMessage("delete user?")
            deleteDialog.setNegativeButton("NO"){ _, _ ->
                deleteDialog.setCancelable(true)
            }
            deleteDialog.setPositiveButton("YES"){ _, _ ->
                deleteUser()
            }
            deleteDialog.show()
        }

        //get intent data
        val data = intent
        activityAction = data.action!!
        //import user data if activityAction = update
        if (activityAction == "update"){
            id = data.getStringExtra("id")!!
            getUserById()
        }

    }

    //get user data by user id
    private fun getUserById(){
        lifecycleScope.launchWhenResumed {
            val result = try {
                client.query(UserByIdQuery(id)).execute()
            }catch (e: ApolloException){
                Toast.makeText(this@UserTreatment, R.string.protocol_error, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            user = result.data?.users_by_pk!!
            if (result.hasErrors()){
                val message = result.errors?.get(0)?.message
                Toast.makeText(this@UserTreatment, message, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }else {
                binding.etName.setText(user.name)
                binding.etRocket.setText(user.rocket)
                binding.etTwitter.setText(user.twitter)
                binding.btnDelete.visibility = View.VISIBLE
            }
        }
    }

    //insert new user
    private fun insertUser(){
        lifecycleScope.launchWhenResumed {
            val result = try {
                client.mutation(InsertUserMutation(
                    binding.etName.text.toString(),
                    binding.etRocket.text.toString(),
                    binding.etTwitter.text.toString()
                )).execute()
            }catch (e: ApolloException){
                Toast.makeText(this@UserTreatment, R.string.protocol_error, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            val affectedRows = result.data?.insert_users!!.affected_rows
            if (result.hasErrors()){
                val message = result.errors?.get(0)?.message
                Toast.makeText(this@UserTreatment, message, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }else {
                if (affectedRows == 0){
                    Toast.makeText(this@UserTreatment, R.string.insert_error, Toast.LENGTH_LONG).show()
                }else {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    //update existing user
    private fun updateUser(){
        lifecycleScope.launchWhenResumed {
            val result = try {
                client.mutation(UpdateUserMutation(
                    id,
                    binding.etName.text.toString(),
                    binding.etRocket.text.toString(),
                    binding.etTwitter.text.toString()
                )).execute()
            }catch (e: ApolloException){
                Toast.makeText(this@UserTreatment, R.string.protocol_error, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            val affectedRows = result.data?.update_users!!.affected_rows
            if (result.hasErrors()){
                val message = result.errors?.get(0)?.message
                Toast.makeText(this@UserTreatment, message, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }else {
                if (affectedRows == 0){
                    Toast.makeText(this@UserTreatment, R.string.update_error, Toast.LENGTH_LONG).show()
                }else {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    //delete existing user
    private fun deleteUser(){
        lifecycleScope.launchWhenResumed {
            val result = try {
                client.mutation(DeleteUserMutation(id)).execute()
            }catch (e: ApolloException){
                Toast.makeText(this@UserTreatment, R.string.protocol_error, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            val affectedRows = result.data!!.delete_users!!.affected_rows
            if (result.hasErrors()){
                val message = result.errors?.get(0)?.message
                Toast.makeText(this@UserTreatment, message, Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }else {
                if (affectedRows == 0){
                    Toast.makeText(this@UserTreatment, R.string.delete_error, Toast.LENGTH_LONG).show()
                }else {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

}