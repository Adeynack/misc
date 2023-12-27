package syncres

type SafeQueue[T any] struct {
	resource *SyncedResource[[]T]
}

func NewQueue[T any](initialSlice []T) *SafeQueue[T] {
	return &SafeQueue[T]{New[[]T](&initialSlice)}
}

func (slice *SafeQueue[T]) Queue(item T) {
	SideEffect(slice.resource, func(syncedSlice *[]T) {
		*syncedSlice = append(*syncedSlice, item)
	})
}

func (slice *SafeQueue[T]) Dequeue() (value T, ok bool) {
	return ModifyAndOk[[]T, T](slice.resource, func(syncedSlice *[]T) (value T, ok bool) {
		return (*syncedSlice)[0], true
	})
}

func (slice *SafeQueue[T]) Len() int {
	length, _ := ModifyAndGet[[]T, int](slice.resource, func(syncedSlice *[]T) (int, error) {
		return len(*syncedSlice), nil
	})
	return length
}
