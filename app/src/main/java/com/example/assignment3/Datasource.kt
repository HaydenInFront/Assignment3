package com.example.assignment3

class Datasource {
    private var draft1Output = "loading"
    private var draft2Output = "loading"
    private var draft3Output = "loading"

    fun importPromptOutputs(list: List<String>) {
        draft1Output = list[0]
        draft2Output = list[1]
        draft3Output = list[2]
    }

    fun loadPromptOutputs(): List<PromptOutput> {
        return listOf<PromptOutput>(
            PromptOutput(R.string.draft1Title, draft1Output),
            PromptOutput(R.string.draft2Title, draft2Output),
            PromptOutput(R.string.draft3Title, draft3Output)
        )
    }
}