package com.example.assignment3

class Datasource {

    fun loadPromptInputs(): List<PromptInput> {
        return listOf<PromptInput>(
            PromptInput(R.string.promptInput1, R.drawable.grey_text_border),
            PromptInput(R.string.promptInput2, R.drawable.grey_text_border)
        )
    }
}