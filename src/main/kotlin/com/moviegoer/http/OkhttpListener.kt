package com.moviegoer.http

import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

class OkhttpListener : EventListener() {
    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        log("connectFailed: [$call, $inetSocketAddress, $proxy, $ioe]")
    }

    override fun connectionReleased(call: Call, connection: Connection) {
        log("connectionReleased: [$call, $connection]")
    }

    override fun requestFailed(call: Call, ioe: IOException) {
        log("requestFailed: [$call, $ioe]")
    }

    override fun requestBodyStart(call: Call) {
        log("requestBodyStart: [$call]")
    }

    override fun responseBodyStart(call: Call) {
        log("responseBodyStart: [$call]")
    }

    override fun secureConnectStart(call: Call) {
        log("secureConnectStart: [$call]")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: MutableList<InetAddress>) {
        log("dnsEnd: [$call, $domainName, ${inetAddressList[0]}]")
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        log("requestHeadersEnd: [$call, $request]")
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        log("responseHeadersEnd: [$call, $response]")
    }

    override fun responseHeadersStart(call: Call) {
        log("responseHeadersStart: [$call]")
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        log("connectionAcquired: [$call, $connection]")
    }

    override fun responseFailed(call: Call, ioe: IOException) {
        log("responseFailed: [$call, $ioe]")
    }

    override fun callEnd(call: Call) {
        log("callEnd: [$call]")
    }

    override fun requestHeadersStart(call: Call) {
        log("requestHeadersStart: [$call]")
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        log("requestBodyEnd: [$call, $byteCount]")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        log("callFailed: [$call, $ioe]")
    }

    override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
        log("connectEnd: [$call, $inetSocketAddress, $proxy]")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        log("connectStart: [$call, $inetSocketAddress, $proxy]")
    }

    override fun callStart(call: Call) {
        log("callStart: [$call]")
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        log("responseBodyEnd: [$call, $byteCount]")
    }

    override fun dnsStart(call: Call, domainName: String) {
        log("dnsStart: [$call, $domainName]")
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        log("secureConnectEnd: [$call, $handshake]")
    }

    private fun log(content: String) {
        println("{OKHTTP} --> $content")
    }
}