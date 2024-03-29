package com.yashendra.todoapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import com.yashendra.todoapp.databinding.ActivityMainBinding
import com.yashendra.todoapp.Fragments.FinishedFragment
import com.yashendra.todoapp.Fragments.HomeFragment
import com.yashendra.todoapp.Utills.Utills
import com.yashendra.todoapp.activities.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var userimage: de.hdodenhof.circleimageview.CircleImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val toolbar=findViewById(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        setSupportActionBar(binding.toolbar)

        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        initialDrawer()

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //setting user info in header
        val headerview=navigationView.getHeaderView(0)
        username=headerview.findViewById(R.id.username) as TextView
        email=headerview.findViewById(R.id.useremail) as TextView
        userimage=headerview.findViewById(R.id.avatar) as de.hdodenhof.circleimageview.CircleImageView

        getUserProfile()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.nav_finishedtask -> {
                    val fragment = FinishedFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.nav_logout -> {
                    val sharedPreferences = getSharedPreferences("TodoPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun getUserProfile() {
        val url="https://fantastic-cyan-katydid.cyclic.app/api/todo/auth"
        val token=Utills.getToken(this)
        //crete a get request here using url without
        val request:JsonObjectRequest=object :JsonObjectRequest(Method.GET,url,null,
            {
                if (it.getBoolean("success")){
                    val user=it.getJSONObject("user")
                    username.text=user.getString("username")
                    email.text=user.getString("email")
                    Picasso.get().load(user.getString("avatar")).placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24).into(userimage)
                }
                else{
                    Toast.makeText(this, "unable to check authorization from server", Toast.LENGTH_SHORT).show()
                }

            },
            {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["Authorization"]="$token"
                return headers
            }
        }
        val requestQueue= com.android.volley.toolbox.Volley.newRequestQueue(this)
        requestQueue.add(request)

    }

    private fun initialDrawer() {
        supportFragmentManager.beginTransaction().replace(R.id.content, HomeFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

       when(item.itemId) {
           R.id.action_share -> {
               val intent = Intent(Intent.ACTION_SEND)
               intent.type = "text/plain"
               intent.putExtra(
                   Intent.EXTRA_TEXT,
                   "Hey, Checkout this amazing TODO App Build by Yashendra that helps you to manage your daily tasks"
               )
               startActivity(Intent.createChooser(intent, "Share via"))
               return true
           }
           R.id.refresh -> {
               supportFragmentManager.beginTransaction().replace(R.id.content, HomeFragment()).commit()
               return true
           }

//           return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//               true
//
//           } else {
//               super.onOptionsItemSelected(item)
//           }
       }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}
