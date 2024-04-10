package com.example.assignment3

class Datasource {
    //start info for datasource
    private var draft1Output = "loading"
    private var draft2Output = "loading"
    private var draft3Output = "loading"

    //imports new data set
    fun importPromptOutputs(list: List<String>) {
        draft1Output = list[0]
        draft2Output = list[1]
        draft3Output = list[2]
    }

    //loads current data set with whatever draft output variables are assigned to
    fun loadPromptOutputs(): List<PromptOutput> {
        return listOf<PromptOutput>(
            PromptOutput(R.string.draft1Title, draft1Output),
            PromptOutput(R.string.draft2Title, draft2Output),
            PromptOutput(R.string.draft3Title, draft3Output)
        )
    }
}