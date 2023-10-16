package Com.carlyle.appprevisaodotempo

import Com.carlyle.appprevisaodotempo.constantes.Const
import Com.carlyle.appprevisaodotempo.databinding.ActivityMainBinding
import Com.carlyle.appprevisaodotempo.model.Main
import Com.carlyle.appprevisaodotempo.services.Api
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trocarTema.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){ //Tema Escuro - Dark Mode
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#000000"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_escuro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#000000"))
                window.statusBarColor = Color.parseColor("#000000")
            }else{ //Tema Claro
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#396BCB"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_claro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#FFFFFF"))
                window.statusBarColor = Color.parseColor("#396BCB")
            }
        }

        binding.btBuscar.setOnClickListener {

            val cidade = binding.editBuscarCidade.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build()
                .create(Api::class.java)

            retrofit.weatherMap(cidade,Const.API_KEY).enqueue(object : Callback<Main>{
                override fun onResponse(call: Call<Main>, response: Response<Main>) {
                    if (response.isSuccessful){
                        respostaServidor(response)
                    }else{
                        Toast.makeText(applicationContext,"Cidade inválida!",Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Main>, t: Throwable) {
                    Toast.makeText(applicationContext,"Erro fatal de servidor!",Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }

            })
        }

    }

    override fun onResume() {
        super.onResume()

            binding.progressBar.visibility = View.VISIBLE

        val retrofit: Api = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(Api::class.java)

        retrofit.weatherMap("São Paulo", Const.API_KEY).enqueue( object : Callback<Main>{
            override fun onResponse(call: Call<Main>, response: Response<Main>) {
                if (response.isSuccessful){
                    respostaServidor(response)
                }else{
                    Toast.makeText(applicationContext,"Cidade inválida! ", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE

                }
            }

            override fun onFailure(call: Call<Main>, t: Throwable) {
                Toast.makeText(applicationContext,"Erro fatal de servidor", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE

            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun respostaServidor(response: Response<Main>) {
        val main: JsonObject = response.body()!!.main
        val temp: Any = main.get("temp").toString()
        val tempMin = main.get("temp_min").toString()
        val tempMax = main.get("temp_max").toString()
        val humidity = main.get("humidity").toString()

        val sys = response.body()!!.sys
        val country = sys.get("country").asString
        var pais = ""

        val weather = response.body()!!.weather
        val mainWeather = weather[0].main
        val description = weather[0].description

        val name = response.body()!!.name

        val tempC:Double = (temp.toString().toDouble() - 273.15)
        val tempCMin:Double = (tempMin.toDouble() - 273.15)
        val tempCMax:Double = (tempMax.toDouble() - 273.15)
        val decimalFormat = DecimalFormat("00.00")

        if (country.equals("BR")){
            pais = "Brasil"
        } else if(pais.equals("US")){
            pais = "Estados Unidos"
        }

        if (mainWeather == "Clouds" && description == "few clouds"){
            binding.imgClima.setBackgroundResource(R.drawable.flewclouds)
        }else if (mainWeather == "Clouds" && description == "scattered clouds"){
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        }else if (mainWeather == "Clouds" && description == "broken clouds"){
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        }else if (mainWeather == "Clouds" && description == "overcast clouds"){
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        }else if (mainWeather == "Clear" && description == "clear sky"){
            binding.imgClima.setBackgroundResource(R.drawable.clearsky)
        }else if (mainWeather == "Snow"){
            binding.imgClima.setBackgroundResource(R.drawable.snow)
        }else if (mainWeather == "Rain"){
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        }else if (mainWeather == "Drizzle"){
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (mainWeather == "Thunderstorm"){
            binding.imgClima.setBackgroundResource(R.drawable.trunderstorm)
        }

        val descricaoClima = when(description){
            "clear sky" -> { "Céu limpo" }
            "few clouds" -> { "Poucas nuvens"
            }"scattered clouds" -> { "Nuvens dispersas"
            }"broken clouds" -> { "Nuvens quebradas"
            }"shower rain" -> { "chuva de banho"
            }"rain" -> { "Chuva"
            }"thunderstorm" -> { "Tempestade"
            }"snow" -> { "Neve"
            }else -> { "Névoa"
            }
        }



        binding.txtTemperatura.text = "${decimalFormat.format(tempC)} ℃"
        binding.txtPaisCidade.text = "$pais - $name"

        binding.txtInformacoes1.text = "Clima \n $descricaoClima \n\n Umidade \n $humidity"
        binding.txtInformacoes2.text = "Temp.Min \n ${decimalFormat.format(tempCMin)} \n\n Temp.Max \n ${decimalFormat.format(tempCMax)}"

        binding.progressBar.visibility = View.GONE

    }
}