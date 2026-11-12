package com.calculator.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.calculator.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupButtons()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.standard)
    }

    private fun setupDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_open,
            R.string.navigation_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                }
                R.id.nav_standard -> {
                    // Already on standard calculator
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupButtons() {
        // Digit buttons
        binding.btn0.setOnClickListener { viewModel.onDigitClick("0") }
        binding.btn1.setOnClickListener { viewModel.onDigitClick("1") }
        binding.btn2.setOnClickListener { viewModel.onDigitClick("2") }
        binding.btn3.setOnClickListener { viewModel.onDigitClick("3") }
        binding.btn4.setOnClickListener { viewModel.onDigitClick("4") }
        binding.btn5.setOnClickListener { viewModel.onDigitClick("5") }
        binding.btn6.setOnClickListener { viewModel.onDigitClick("6") }
        binding.btn7.setOnClickListener { viewModel.onDigitClick("7") }
        binding.btn8.setOnClickListener { viewModel.onDigitClick("8") }
        binding.btn9.setOnClickListener { viewModel.onDigitClick("9") }

        // Decimal
        binding.btnDecimal.setOnClickListener { viewModel.onDecimalClick() }

        // Operators
        binding.btnAdd.setOnClickListener { viewModel.onOperatorClick("+") }
        binding.btnSubtract.setOnClickListener { viewModel.onOperatorClick("−") }
        binding.btnMultiply.setOnClickListener { viewModel.onOperatorClick("×") }
        binding.btnDivide.setOnClickListener { viewModel.onOperatorClick("÷") }

        // Equals
        binding.btnEquals.setOnClickListener { viewModel.onEqualsClick() }

        // Clear functions
        binding.btnClear.setOnClickListener { viewModel.onClearClick() }
        binding.btnClearEntry.setOnClickListener { viewModel.onClearEntryClick() }
        binding.btnBackspace.setOnClickListener { viewModel.onBackspaceClick() }

        // Special operations
        binding.btnPercent.setOnClickListener { viewModel.onPercentClick() }
        binding.btnSquare.setOnClickListener { viewModel.onSquareClick() }
        binding.btnSquareRoot.setOnClickListener { viewModel.onSquareRootClick() }
        binding.btnFraction.setOnClickListener { viewModel.onFractionClick() }
        binding.btnNegate.setOnClickListener { viewModel.onNegateClick() }

        // Memory buttons
        binding.btnMC.setOnClickListener { viewModel.onMemoryClear() }
        binding.btnMR.setOnClickListener { viewModel.onMemoryRecall() }
        binding.btnMPlus.setOnClickListener { viewModel.onMemoryAdd() }
        binding.btnMMinus.setOnClickListener { viewModel.onMemorySubtract() }
        binding.btnMS.setOnClickListener { viewModel.onMemoryStore() }
    }

    private fun observeViewModel() {
        viewModel.displayText.observe(this) { text ->
            binding.tvDisplay.text = text
        }

        viewModel.expressionText.observe(this) { text ->
            binding.tvExpression.text = text
        }

        viewModel.memoryActive.observe(this) { isActive ->
            binding.btnMC.isEnabled = isActive
            binding.btnMR.isEnabled = isActive
            binding.btnMC.alpha = if (isActive) 1.0f else 0.4f
            binding.btnMR.alpha = if (isActive) 1.0f else 0.4f
        }
    }
}
