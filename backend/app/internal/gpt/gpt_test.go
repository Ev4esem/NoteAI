package gpt

import (
	"io/ioutil"
	"log"
	"testing"
)

func TestReqIp(t *testing.T) {
	const urlIP string = "https://api.ipify.org"

	client := genClientProxy()

	resp, err := client.Get(urlIP)
	if err != nil {
		log.Fatal(err)
	}
	defer resp.Body.Close()
	body, _ := ioutil.ReadAll(resp.Body)
	log.Println(string(body))
}
