package com.example.sd01

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.sd01.databinding.FragmentFirstBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var _spinner: Spinner? = null
    private val spinner get() = _spinner!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener { view ->

            submitTxt2Img()
        }

        _spinner = binding.modelSpinner
        binding.mentionLabel.text = modelLoad

        Init()
        return binding.root

    }
    var isSubmitting = false
    var isInit = false
    var changeModeling = false
    var options: OptionResponse? = null
    var modelsList: ArrayList<String>? = null
    var modelIndex = 0

    val modelLoad = "Model is OK!"
    val modelLoading = "Model is loading..."

    private fun Init(){
        isInit = true

        getOptions()
    }

    private fun getOptions() {
        val apiService = RestApiService()
        apiService.getOptions(::optionsInit)
    }

    private fun optionsInit(res: OptionResponse?){
        options = res
        getModels()
    }

    private fun modelsInit(res: List<ModelsResponse>?){
        modelsList = ArrayList(res?.map { it.title })
// Create an ArrayAdapter using the string array and a default spinner layout

        spinner.adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, modelsList!!)

        var index = modelsList!!.indexOfFirst { it == options?.model }
        modelIndex = index
        setModelsSpinner(index)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                changeModels(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                setModelsSpinner(modelIndex)
            }

        }

        isInit = false
    }

    private fun changeModels(index:Int){
        val modelName = modelsList!![index]
        if (modelName == options?.model) return

        options?.model = modelName
        //现在不需要完全修改服务器的model， 单次请求单次单独的model
//        val apiService = RestApiService()
//        var op:OptionResponse = OptionResponse(
//            model = modelName!!
//        )
//        changeModeling = true
//        binding.mentionLabel.text = modelLoading
//        apiService.setOptions(op, ::SetOptionResponse)
    }

    fun SetOptionResponse(res: Any?) {
        if (res == null){
            changeModeling = false
            binding.mentionLabel.text = modelLoad
        }
    }

    private fun getModels(){
        val apiService = RestApiService()
        apiService.getModels(::modelsInit)
    }

    private fun setModelsSpinner(index: Int){
        spinner.setSelection(index)
    }


    private fun submitTxt2Img(){
        if (isInit || changeModeling) return
        isSubmitting = true
        _binding?.button?.text = "Wait..."
//        val apiService = RestApiService()
        val prompt = _binding?.promptsText?.text.toString()
        val steps = _binding?.StepsText?.text.toString().toIntOrNull() ?: 0
        val option = OptionData(
            model = options?.model
        )

        val arg = ConnectUtil.getArgs2(true, null,null,null,null)

        val userInfo = UserInfo(
            prompt = prompt,
            steps = steps,
            option = option,
            args = arg
        )
        val apiService = RestApiService()
        apiService.postTxt2Img(userInfo, ::showImage)
    }

    private fun showImage(res : Txt2ImgResponse?){
        _binding?.button?.text = "SUBMIT"
        isSubmitting = false

        if (res == null) return
        val base64String = res?.images?.get(0)
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        _binding?.imageView?.setImageBitmap(decodedImage)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}