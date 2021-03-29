package com.example.hsaplanner

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.hsaplanner.data.BalanceDataSource
import com.example.hsaplanner.data.Expense
import com.example.hsaplanner.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.time.LocalDate

const val TAG = "TAG"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val expenses = listOf(
            Expense(
                "Expense 1",
                LocalDate.of(2018, 12, 17),
                BigDecimal("6098.56"),
                BigDecimal("3098.56")
            ),
            Expense(
                "Expense 2",
                LocalDate.of(2021, 1, 12),
                BigDecimal("2833.72"),
                BigDecimal("2833.72")
            )
        )

        val balanceLines = BalanceDataSource(this, expenses).load()

        val recyclerView = binding.balanceRecyclerView
        recyclerView.adapter = BalanceLineAdapter(balanceLines)
        recyclerView.setHasFixedSize(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}