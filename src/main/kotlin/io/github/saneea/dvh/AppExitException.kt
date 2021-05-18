package io.github.saneea.dvh

class AppExitException @JvmOverloads constructor(val exitCode: Int, cause: Throwable? = null) :
    RuntimeException(cause) {

    interface ExitCode {
        companion object {
            const val OK = 0
            const val ERROR = 1
        }
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1L
    }
}