package testdata

import (
	"bytes"
	"encoding/hex"
	"fmt"
	"io/ioutil"
	"math"
	"strings"
	"testing"
	"time"

	"github.com/pascaldekloe/goe/verify"
)

type golden struct {
	serial string
	object O
}

func newGoldenCases() []*golden {
	return []*golden{
		{"7f", O{}},
		{"007f", O{B: true}},
		{"01017f", O{U32: 1}},
		{"01ff017f", O{U32: math.MaxUint8}},
		{"01ffff037f", O{U32: math.MaxUint16}},
		{"81ffffffff7f", O{U32: math.MaxUint32}},
		{"02017f", O{U64: 1}},
		{"02ff017f", O{U64: math.MaxUint8}},
		{"02ffff037f", O{U64: math.MaxUint16}},
		{"02ffffffff0f7f", O{U64: math.MaxUint32}},
		{"82ffffffffffffffff7f", O{U64: math.MaxUint64}},
		{"03017f", O{I32: 1}},
		{"83017f", O{I32: -1}},
		{"037f7f", O{I32: math.MaxInt8}},
		{"8380017f", O{I32: math.MinInt8}},
		{"03ffff017f", O{I32: math.MaxInt16}},
		{"838080027f", O{I32: math.MinInt16}},
		{"03ffffffff077f", O{I32: math.MaxInt32}},
		{"8380808080087f", O{I32: math.MinInt32}},
		{"04017f", O{I64: 1}},
		{"84017f", O{I64: -1}},
		{"047f7f", O{I64: math.MaxInt8}},
		{"8480017f", O{I64: math.MinInt8}},
		{"04ffff017f", O{I64: math.MaxInt16}},
		{"848080027f", O{I64: math.MinInt16}},
		{"04ffffffff077f", O{I64: math.MaxInt32}},
		{"8480808080087f", O{I64: math.MinInt32}},
		{"04ffffffffffffffff7f7f", O{I64: math.MaxInt64}},
		{"848080808080808080807f", O{I64: math.MinInt64}},
		{"05000000017f", O{F32: math.SmallestNonzeroFloat32}},
		{"057f7fffff7f", O{F32: math.MaxFloat32}},
		{"057fc000007f", O{F32: float32(math.NaN())}},
		{"0600000000000000017f", O{F64: math.SmallestNonzeroFloat64}},
		{"067fefffffffffffff7f", O{F64: math.MaxFloat64}},
		{"067ff80000000000017f", O{F64: math.NaN()}},
		{"0755ef312a2e5da4e77f", O{T: time.Unix(1441739050, 777888999).In(time.UTC)}},
		{"870000000100000000000000007f", O{T: time.Unix(math.MaxUint32+1, 0).In(time.UTC)}},
		{"87ffffffffffffffff2e5da4e77f", O{T: time.Unix(-1, 777888999).In(time.UTC)}},
		{"87fffffff14f443f00000000007f", O{T: time.Unix(-63094636800, 0).In(time.UTC)}},
		{"0801417f", O{S: "A"}},
		{"080261007f", O{S: "a\x00"}},
		{"0809c280e0a080f09080807f", O{S: "\u0080\u0800\U00010000"}},
		{"08800120202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020207f", O{S: strings.Repeat(" ", 128)}},
		{"0901ff7f", O{A: []byte{math.MaxUint8}}},
		{"090202007f", O{A: []byte{2, 0}}},
		{"09c0010909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909090909097f", O{A: bytes.Repeat([]byte{9}, 192)}},
		{"0a7f7f", O{O: &O{}}},
		{"0a007f7f", O{O: &O{B: true}}},
		{"0b01007f7f", O{Os: []*O{{B: true}}}},
		{"0b027f7f7f", O{Os: []*O{{}, {}}}},
		{"0c0300016101627f", O{Ss: []string{"", "a", "b"}}},
		{"0d0201000201027f", O{As: [][]byte{[]byte{0}, []byte{1, 2}}}},
		{"0e017f", O{U8: 1}},
		{"0eff7f", O{U8: math.MaxUint8}},
		{"8f017f", O{U16: 1}},
		{"0fffff7f", O{U16: math.MaxUint16}},
		{"1002000000003f8000007f", O{F32s: []float32{0, 1}}},
		{"11014058c000000000007f", O{F64s: []float64{99}}},
	}
}

func TestMarshal(t *testing.T) {
	for _, gold := range newGoldenCases() {
		data, err := gold.object.MarshalBinary()
		if err != nil {
			t.Errorf("0x%s: %s", gold.serial, err)
			continue
		}
		if got := hex.EncodeToString(data); got != gold.serial {
			t.Errorf("Got 0x%s, want 0x%s", got, gold.serial)
		}
	}
}

func TestUnmarshal(t *testing.T) {
	for _, gold := range newGoldenCases() {
		data, err := hex.DecodeString(gold.serial)
		if err != nil {
			t.Fatal(err)
		}

		got := O{}
		if err := got.UnmarshalBinary(data); err != nil {
			t.Errorf("0x%s: %s", gold.serial, err)
			continue
		}
		verify.Values(t, fmt.Sprintf("0x%s", gold.serial), got, gold.object)
	}
}

// TestFuzzSeed updates the initial input corpus for fuzz testing.
func TestFuzzSeed(t *testing.T) {
	for _, gold := range newGoldenCases() {
		data, err := gold.object.MarshalBinary()
		if err != nil {
			t.Fatal(err)
		}
		if ioutil.WriteFile("testdata/corpus/seed"+gold.serial, data, 0644); err != nil {
			t.Fatal(err)
		}
	}
}
