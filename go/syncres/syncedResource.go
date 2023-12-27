package syncres

import "sync"

type SyncedResource[T any] struct {
	resource *T
	mutex    *sync.Mutex
}

func New[T any](initialValue *T) *SyncedResource[T] {
	return &SyncedResource[T]{resource: initialValue}
}

// ModifyAndGet ensures that function `fn` is executed in a context
// where the resource is protected by its mutex and returns
// the return value of `fn`, which is any value and an optional error.
func ModifyAndGet[T any, TReturn any](resource *SyncedResource[T], fn func(syncedRes *T) (TReturn, error)) (TReturn, error) {
	resource.mutex.Lock()
	defer resource.mutex.Unlock()
	return fn(resource.resource)
}

// ModifyAndOk ensures that function `fn` is executed in a context
// where the resource is protected by its mutex and returns
// the return value of `fn`, which is any value and a boolean flag
// indicating if the operation was successful.
func ModifyAndOk[T any, TReturn any](resource *SyncedResource[T], fn func(syncedRes *T) (TReturn, bool)) (TReturn, bool) {
	resource.mutex.Lock()
	defer resource.mutex.Unlock()
	return fn(resource.resource)
}

// Modify ensures that function `fn` is executed in a context
// where the resource is protected by its mutex and returns
// the return value of `fn`.
func Modify[T any](resource *SyncedResource[T], fn func(syncedRes *T) error) error {
	resource.mutex.Lock()
	defer resource.mutex.Unlock()
	return fn(resource.resource)
}

// SideEffect ensures that function `fn` is executed in a context
// where the resource is protected by its mutex. It returns nothing.
// Any error management has to be done inside of `fn`.
func SideEffect[T any](resource *SyncedResource[T], fn func(syncedRes *T)) {
	resource.mutex.Lock()
	defer resource.mutex.Unlock()
	fn(resource.resource)
}
