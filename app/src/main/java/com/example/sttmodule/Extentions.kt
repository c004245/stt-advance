package com.example.sttmodule

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable {
    compositeDisposable.add(this)
    return this
}