package com.trendyol.kediatr

import com.trendyol.kediatr.common.RegistryImpl

class CommandBusImpl(private val registry: RegistryImpl, private val publishStrategy: PublishStrategy = StopOnExceptionPublishStrategy()) : CommandBus {
    override fun <TQuery : Query<TResponse>, TResponse> executeQuery(query: TQuery): TResponse = processPipeline(registry.getPipelineBehaviors(), query) {
        registry.resolveQueryHandler(query.javaClass).handle(query)
    }

    override fun <TCommand : Command> executeCommand(command: TCommand) = processPipeline(registry.getPipelineBehaviors(), command) {
        registry.resolveCommandHandler(command.javaClass).handle(command)
    }

    override fun <T : Notification> publishNotification(notification: T) = processPipeline(registry.getPipelineBehaviors(), notification) {
        publishStrategy.publish(notification, registry.resolveNotificationHandlers(notification.javaClass))
    }

    override suspend fun <TQuery : Query<TResponse>, TResponse> executeQueryAsync(query: TQuery): TResponse = processAsyncPipeline(registry.getAsyncPipelineBehaviors(), query) {
        registry.resolveAsyncQueryHandler(query.javaClass).handleAsync(query)
    }

    override suspend fun <TCommand : Command> executeCommandAsync(command: TCommand) = processAsyncPipeline(registry.getAsyncPipelineBehaviors(), command) {
        registry.resolveAsyncCommandHandler(command.javaClass).handleAsync(command)
    }

    override suspend fun <T : Notification> publishNotificationAsync(notification: T) = processAsyncPipeline(registry.getAsyncPipelineBehaviors(), notification) {
        publishStrategy.publishAsync(notification, registry.resolveAsyncNotificationHandlers(notification.javaClass))
    }
}