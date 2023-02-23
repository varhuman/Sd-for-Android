package com.example.sd01

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

object ConnectUtil {
    public fun getArgs(open: Boolean, m1Model: String?, m1Weight: Int?, m2Model: String?, m2Weight: Int?): ArgsData {
        var test = "lora-hanfugirl-v1-5(c97665df9b71)"
        var args = ArgsData(
            addNetEnabled = open,
            addNetSeparateWeights = false,
            addNetModule1 = "LoRA",
            addNetModel1 = test ?: "",
            addNetWeightA1 = m1Weight ?: 1,
            addNetWeightB1 = m1Weight ?: 1,
            addNetModule2 = "LoRA",
            addNetModel2 = m2Model ?: "",
            addNetWeightA2 = m2Weight ?: 1,
            addNetWeightB2 = m2Weight ?: 1,
        )
        return args
    }

    public fun getArgs2(open: Boolean, m1Model: String?, m1Weight: Int?, m2Model: String?, m2Weight: Int?): JsonArray {
        var test = "lora-hanfugirl-v1-5(c97665df9b71)"
        var test2 = "dingzhenlora_v1(fa7c1732cc95)"

        var res = JsonArray()
        res.add(0)
        res.add(true)
        res.add(true)
        res.add("LoRA")
        res.add(test2)
        res.add(1)
        res.add(1)
        return res
    }

    private fun ArgsData(addNetEnabled: Boolean): ArgsData {
        return ArgsData(
            addNetEnabled = addNetEnabled,
            addNetSeparateWeights = false,
            addNetModule1 = "",
            addNetModel1 = "",
            addNetWeightA1 = 0,
            addNetWeightB1 = 0,
            addNetModule2 = "",
            addNetModel2 = "",
            addNetWeightA2 = 0,
            addNetWeightB2 = 0,
        )
    }
}