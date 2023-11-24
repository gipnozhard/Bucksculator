package com.lavrent.bucksculator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.core.view.children
import com.lavrent.bucksculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    //binding
    private lateinit var binding : ActivityMainBinding //  переменная для привязки макета действий

    //other
    private var firstNumber = "" //первый номер
    private var currentNumber = ""  //текущий номер
    private var currentOperator = "" //текущий оператор
    private var result = "" //результат


    //После того, как у вас это будет установлено, то что снизу,
    // вы сможете получить доступ к своим представлениям,
    // просто написав binding.ViewID, и это эквивалентно тому,
    // как вы используете find view по id.
    // Если вы хотите получить доступ к нескольким представлениям
    // одновременно с помощью Kotlin, просто используйте apply.
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //NoLimitScreen
        window.setFlags(   // устанавливается флаг (включатель.выключатель) функции,
            // на данном этапе окна приложения(это абстрактный базовый класс пользовательского интерфейса)
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, // установка значения флага(позволяет окну выходить за пределы экрана)
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS  // вторым параметром является маска.
        // Это позволяет вам включать и выключать поля одновременно, и изменяться будут только биты в маске.
        // ТАКИМ образом, любая другая настройка будет сохранена такой, какой она была.
        )

        //initViews , блок инициализации функционала видов
        binding.apply {

            //get all buttons , получение каждой кнопки из списка
            binding.layoutMain.children.filterIsInstance<Button>().forEach { button ->

                //buttons click listener
                button.setOnClickListener { // слушатель нажатий кнопки

                    //get clicked button text
                    val buttonText = button.text.toString()
                    when{
                        buttonText.matches(Regex("[0-9]")) -> { // Пытается сопоставить текст кнопки ( со слушателя нажатий) с регулярным значением 0 - 9
                            if (currentOperator.isEmpty()) { // если текущий оператор пустой (не нажат)
                                firstNumber += buttonText // первый номер(это просто переменная 1 от 0 - 9) складывается с нажатым на на кнопку номером
                                tvResult.text = firstNumber // получение результатов в поле textView, где описываются результаты вычеслений на калькуляторе.
                            } else { // иначе, если не пустое значение оператора есть + или - или /, *
                                currentNumber += buttonText // текущий номер(это просто переменная 2 от 0 - 9) складывается складывается с нажатым на кнопку значением (полученный при нажатии)
                                tvResult.text = currentNumber // запись результата в поле textView, где описываются результаты вычеслений на калькуляторе.
                            }
                        }
                        buttonText.matches(Regex("[+\\-*/]")) -> { // Пытается сопоставить текст кнопки ( со слушателя нажатий) с регулярным значением + или - или /, *
                            currentNumber = "" // текущий номер (равный
                            // пустому значению) остается при нажатии, но ...
                            if (tvResult.text.toString().isNotEmpty()) { // если результат нажатия на кнопки 0 - 9 которые выводятся в textView, окно резултатов не пустое
                                currentOperator = buttonText // то в переменную "текущий оператор" записывается +, -, / или * в завиимости от нажатия
                                tvResult.text = "0" // а в результат, где textView  для вычеслений, выводится 0
                            }
                        }
                        buttonText == "=" -> { // при нажатии на знак =
                            if (currentNumber.isNotEmpty() && currentOperator.isNotEmpty()) { // если текущий омер не пустой и текущий оператор не пустой то
                                tvFormula.text = "$firstNumber$currentOperator$currentNumber" // певый номер, оператор текущий и текущий номер выводится в виде формулы рассчета в textView выше, где вычисляется ответ
                                result = evaluateExpression(firstNumber, currentNumber, currentOperator) // результат вычисления выражения функции
                                firstNumber = result // записываем последнее результирующее значение в значение первого номера, для продолжения вычисления
                                tvResult.text = result // вывод результата в textView, выводится результат
                            }
                        }
                        buttonText == "." -> { // при нажатии на точку
                            if (currentOperator.isEmpty()) { // если текущий оператор пустой
                                if (!firstNumber.contains(".")) // если первый номер не содержит .
                                {
                                    if (firstNumber.isEmpty()) { // если первый номер пустой
                                        firstNumber += "0$buttonText" // первый номер складывается с 0.
                                    } else { // иначе
                                        firstNumber += buttonText // первый номер складывается с точкой
                                    }
                                    tvResult.text = firstNumber // значние выводится на textView, где результат выводится
                                }
                            } else { // иначе если не пустой текущий оператор
                                if (!currentNumber.contains(".")) { // если текущий номер не содержит .
                                    if (currentNumber.isEmpty()) { // если текущий номер пустой
                                        currentNumber += "0$buttonText" // текущий номер складывется и равен 0.
                                    } else { // иначе если не пустой
                                        currentNumber += buttonText // текущий номер равен и складывается с точкой
                                        tvResult.text = currentNumber // результат выводится на textView, где производится вывод вычесления
                                    }
                                }
                            }
                        }

                        buttonText == "C" -> { // при нажатии на кнопку C , все значения становятся пустым значением,
                            // где после этого можно,
                            // как с чистого листа произвести новые интересные функции
                            currentNumber = ""
                            firstNumber = ""
                            currentOperator = ""
                            tvResult.text = ""
                            tvFormula.text = ""
                        }
                    }
                }
            }
        }
    }

    //functions
    private fun evaluateExpression(firstNumber : String,  secondNumber : String, operator : String): String { // функция вычисления выражения
        val num1 = firstNumber.toDouble() // первый номер с плавающей точкой
        val num2 = secondNumber.toDouble() // второй номер с плавающей точкой

        return when(operator) { // возвращает значение оператора:
            "+" -> {
                (num1 + num2).toString() // производится функция сложения
            }
            "-" -> {
                (num1 - num2).toString() // производится функция вычитания
            }
            "*" -> {
                (num1 * num2).toString() // производится функция произведения
            }
            "/" -> {
                (num1 / num2).toString() // производится функция деления
            }
            else -> {
                ""
            }
        }
    }
}