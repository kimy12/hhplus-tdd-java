# 동시성 제어 방식에 대한 분석
***************

## 배경
동시성 이슈를 해결 하기 위해 서는 여러 가지 방법이 존재 하는데 크게 어플리케이션 레벨, DB, Redis 활용이 있다.

현 프로젝트에는 db나 Redis를 활용하지 않고, table class로 대체하여 쓰고 있기 때문에 사실상 동시성 제어 방식은 어플리케이션 레벨에서의 고민이 필요했다.

## 문제 상황
PointService의 비지니스 로직에서 [조회 -> 포인트 사용] , [조회 -> 포인트 충전] 부분에서 동시성 이슈가 발생한다. 

동시에 읽고 쓰는 등의 트랜잭션 작업을 성공적으로 마칠 수 있도록 동시에 작업이 일어나지 않게 하고, 부가적으로는 순서대로 작업할 수 있도록 하는 방법을 찾아야 한다.

## 해결과정

### 1. synchronized  
> 메서드나 객체 변수에 사용 가능하다. 
> 오직 한 개의 스레드에서만 접근 가능 하며 다른 스레드는 Blocked 상태로 대기하게 되어, 남발하게 되면 성능 저하 이슈가 있다.
> 또한 synchronized은 공정성을 지원 하지 않아 다른 스레드 들에게 우선순위가 밀려 기아 상태 (Starvation)가 발생 할 수 있다.

이러한 이유로 인해 synchronized는 제외하기로 했다.

### 2. ConcurrentHashMap
> hashtable 클래스의 경우, 대부분의 method에 synchronized 가 사용 되어 있다.
> Multi-thread에서 동시성 문제는 해결 가능 하지만, 위에 언급했다시피 synchronized가 남발되어 병목현상이 발생할 수 있다. (심지어 해당 클래스는 Collection framework 이전의 클래스 이다.)
> 
> 이런 단점을 해결하기 위해 Multi-thread 환경에서 사용 할 수 있도록 나온 클래스가 ConcurrentHashMap 이다.

### 3. ReentrantLock
> ReentrantLock은 synchronized와 동일한 기본 동작 및 의미를 가지지만, 타임 아웃 지정 가능, condition 지정을 통해 대기 중인 스레드를 선별적으로 깨우는 등 확장된 기능을 제공 한다. 
> 
> 동일한 스레드가 이미 획득한 락을 다시 요청 하면 별도의 대기 없이 락을 다시 획득 가능하여, 
> 또한, synchronized는 공정성을 지원하지 않지만, ReentrantLock는 생성자의 인자를 true로 준다면 각 스레드에 공정성을 지원해 주기도 한다. (하지만 공정한 락을 사용하는 프로그램은 많은 스레드가 접근할 경우 어떤 스레드가 가장 오래 기다렸는지 확인하는 작업이 필요하여 성능이 떨어질 수 밖에 없다고 한다.)
> 주의사항으로는 finally구문에서 반드시 unlock() 으로 락을 해제해 주어야 한다.

### 4. ConcurrentHashMap + ReentrantLock 병렬 사용
> 이번에는, 단일 객체의 동시성을 처리하는 동안, 특정 키 (ID)에 대한 복합 작업을 안전하게 수행하려면 ReentrantLock을 병행 사용 하려고 한다.
> 
> 포인트를 충전 및 사용 하는 로직에서는, ReentrantLock를 사용하여, ConcurrentHashMap 을 통해 개별 키를 기준으로 세밀하게 락을걸어, 읽기, 쓰기 등 다른 복잡한 작업을 포함에 thread-safe하게 처리 할 수 있다.


## 결론
이번 동시성 문제는, 어플리케이션 레벨에서의 해결방법으로만 고민하는 시간을 가졌다. 추후에는, db레벨에서의 해결 방법(db락) 고민과 더 나아가서 디스크를 사용하는 데이터베이스보다 더 빠르게 락을 획득 및 해제 할 수 있다는 Redis로 동시성을 제어하는 방법까지
공부해 보는 시간을 가져야겠다.