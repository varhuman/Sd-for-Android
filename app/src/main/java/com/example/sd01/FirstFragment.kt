package com.example.sd01

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sd01.databinding.FragmentFirstBinding
import com.google.gson.JsonArray


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var _spinner: Spinner? = null
    private val modelSpinner get() = _spinner!!

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
    var loras: ArrayList<String>? = null
    var modelsList: ArrayList<String>? = null
    var modelIndex = 0

    val modelLoad = "加载图片会比较慢~"
    val modelLoading = "Model is loading..."

    private fun Init(){
        isInit = true

        getOptions()

        getLora()
    }

    private fun getLora() {
        val apiService = RestApiService()
        apiService.getLoras(::lorasInit)
    }

    private fun getOptions() {
        val apiService = RestApiService()
        apiService.getOptions(::optionsInit)
    }

    private fun lorasInit(res: LoraModelResponse?){
        loras = res?.loras
        setLoraSpinners()
    }

    private fun optionsInit(res: OptionResponse?){
        options = res
        getModels()
    }

    private fun modelsInit(res: List<ModelsResponse>?){
        modelsList = ArrayList(res?.map { it.title })
// Create an ArrayAdapter using the string array and a default spinner layout

        setSpinner(modelSpinner, modelsList!!)
        var index = modelsList!!.indexOfFirst { it == options?.model }
        modelIndex = index
        setModelsSpinner(index)
        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

    private fun setLoraSpinners(){
        if (loras == null)
            return
        setSpinner(binding.LoraWList1, loras!!)
        setSpinner(binding.LoraWList2, loras!!)
        setSpinner(binding.LoraWList3, loras!!)
//        setSpinner(binding.LoraWList4, loras!!)
//        setSpinner(binding.LoraWList5, loras!!)


        val setValue :(TextView, String) -> Unit = { text: TextView, value : String -> text.text = value }
        setSeekBarValueChange(binding.LoraWeightBar1,binding.LoraWeightBar1Value , setValue)
        setSeekBarValueChange(binding.LoraWeightBar2,binding.LoraWeightBar2Value , setValue)
        setSeekBarValueChange(binding.LoraWeightBar3,binding.LoraWeightBar3Value , setValue)
//        setSeekBarValueChange(binding.LoraWeightBar4,binding.LoraWeightBar4Value , setValue)
//        setSeekBarValueChange(binding.LoraWeightBar5,binding.LoraWeightBar5Value , setValue)
    }

    private fun setSpinner(spinner: Spinner, list: ArrayList<String>){
        spinner.adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list!!)
    }

    private fun setSeekBarValueChange(seekBar: SeekBar,textView: TextView,  function: (TextView,String) -> Unit){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                function(textView, (progress / 100f).toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
    }

    private fun setModelsSpinner(index: Int){
        modelSpinner.setSelection(index)
    }


    private fun submitTxt2Img(){
        if (isInit || changeModeling || isSubmitting) return
        isSubmitting = true
        _binding?.button?.text = "Wait..."
//        val apiService = RestApiService()
        val prompt = _binding?.PromptText?.text.toString()
        val nprompt = _binding?.npromptText?.text.toString()
        val faceStore = _binding?.FaceRestoreCheckBox?.isChecked
        val seed = _binding?.SeedText?.text.toString().toIntOrNull() ?:-1
//        val prompt = "highres,8k,masterpiece, best quality,instagram most viewed,Megapixel,illustration"
        val steps = _binding?.StepsText?.text.toString().toIntOrNull() ?: 0
        val option = OptionData(
            model = options?.model
        )

        val arg = getLoraJson()
        val userInfo = UserInfo(
            prompt = prompt,
            negativePrompt = nprompt,
            steps = steps,
            option = option,
            args = arg,
            seed = seed,
            restoreFace = faceStore?:true
        )
        val apiService = RestApiService()
        apiService.postTxt2Img(userInfo, ::showImage)
    }

    private fun getLoraJson() : JsonArray{
        var res = JsonArray()
        res.add(0)
        res.add(true)
        res.add(false)
        res.add("LoRA")
        res.add(binding.LoraWList1.selectedItem.toString())
        res.add(binding.LoraWeightBar1.progress / 100f)
        res.add(binding.LoraWeightBar1.progress / 100f)
        res.add("LoRA")
        res.add(binding.LoraWList2.selectedItem.toString())
        res.add(binding.LoraWeightBar2.progress / 100f)
        res.add(binding.LoraWeightBar2.progress / 100f)
        res.add("LoRA")
        res.add(binding.LoraWList3.selectedItem.toString())
        res.add(binding.LoraWeightBar3.progress / 100f)
        res.add(binding.LoraWeightBar3.progress / 100f)
        return res
    }

    private fun showImage(res : Txt2ImgResponse?){
        _binding?.button?.text = "SUBMIT"
        isSubmitting = false

        if (res == null) return
        val base64String = res?.images?.get(0)
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        _binding?.imageView?.setImageBitmap(decodedImage)
//        Util.saveToInternalStorage(decodedImage, "test", requireContext())
        Util.saveToGallery(decodedImage, "test", requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}