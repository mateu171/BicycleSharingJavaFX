package org.example.bicyclesharing.repository.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public final class CachedRepositoryProxy {

  private CachedRepositoryProxy() {
  }

   @SuppressWarnings("unchecked")
  public static <R,T,ID> R create(R targetRepository,Class<R> repositoryInterface,Function<T,ID> idExtractor)
   {
     IdentityMap<ID,T> cache = new IdentityMap<>();

     InvocationHandler handler = new CacheRepositoryInvocationHandler<>(
         targetRepository,
         idExtractor,
         cache
       );

     return (R) Proxy.newProxyInstance(
         repositoryInterface.getClassLoader(),
         new Class<?>[]{repositoryInterface},
         handler
     );
   }

   private static class CacheRepositoryInvocationHandler<T,ID> implements InvocationHandler
   {
     private final Object targetRepository;
     private final Function<T,ID> idExtractor;
     private final IdentityMap<ID,T> cache;

     private CacheRepositoryInvocationHandler(Object targetRepository, Function<T, ID> idExtractor,
         IdentityMap<ID, T> cache) {
       this.targetRepository = targetRepository;
       this.idExtractor = idExtractor;
       this.cache = cache;
     }

     @Override
     public Object invoke(Object proxy, Method method,Object[] args)throws Throwable
     {
       String methodName = method.getName();

       if (methodName.equals("findById") && args != null && args.length == 1) {
         return handleFindById(method, args[0]);
       }

       if (methodName.equals("save") && args != null && args.length == 1) {
         return handleSaveOrUpdate(method, args);
       }

       if (methodName.equals("update") && args != null && args.length == 1) {
         return handleSaveOrUpdate(method, args);
       }

       if (methodName.equals("deleteById") && args != null && args.length == 1) {
         return handleDeleteById(method, args);
       }

       if (methodName.equals("delete") && args != null && args.length == 1) {
         return handleDelete(method, args);
       }

       if (methodName.equals("existsById") && args != null && args.length == 1) {
         return handleExistsById(method, args[0]);
       }

       Object result = method.invoke(targetRepository,args);
       cacheResultIdPossible(result);

       return result;
     }

     private void cacheResultIdPossible(Object result) {
       if(result instanceof Collection<?> collection)
       {
         for(Object item : collection)
         {
           T entity = (T) item;
           cache.put(idExtractor.apply(entity),entity);
         }
       }
     }

     private Object handleExistsById(Method method, Object id) throws Throwable {
       if (cache.contains((ID) id)) {
         return true;
       }

       return method.invoke(targetRepository, id);
     }

     private Object handleDelete(Method method, Object[] args) throws Throwable {
       T entity = (T) args[0];
       ID id = idExtractor.apply(entity);

       Object result = method.invoke(targetRepository, args);

       if (result instanceof Boolean deleted && deleted) {
         cache.remove(id);
       }

       return result;
     }

     private Object handleDeleteById(Method method, Object[] args) throws Throwable {
       Object result = method.invoke(targetRepository,args);

       if(result instanceof Boolean deleted && deleted)
         cache.remove((ID) args[0]);

       return result;
     }

     private Object handleSaveOrUpdate(Method method, Object[] args) throws Throwable {
       Object result = method.invoke(targetRepository,args);

       T entity = (T) result;
       cache.put(idExtractor.apply(entity),entity);
       return  result;
     }

     private Object handleFindById(Method method, Object id) throws Throwable {
       Optional<T> cached = cache.get((ID) id);

       if(cached.isPresent())
         return  cached;

       Object result = method.invoke(targetRepository,id);
       if(result instanceof Optional<?> optional && optional.isPresent()) {
         T entity = (T) optional.get();
         cache.put(idExtractor.apply(entity), entity);
       }
       return  result;
     }
   }
}
