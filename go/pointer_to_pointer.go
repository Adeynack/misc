// You can edit this code!
// Click here and start typing.
package main

import "fmt"

func main() {
	var isdefined, isnil, isundefined **int

	isdefined = func() **int { t := 42; return &(&t) }()
	isnil = func() **int { var t *int = nil; return &(&t) }()
	isundefined = nil

	fmt.Println(isundefined)
	fmt.Println(isnil)
	fmt.Println(isdefined)
}

func convert(value **int) string {
	if value == nil {
		return "undefined"
	}
	if *value == nil {
		return "null"
	}
	return **value
}
