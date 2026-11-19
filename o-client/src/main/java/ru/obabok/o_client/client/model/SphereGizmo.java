package ru.obabok.o_client.client.model;

import net.minecraft.gizmos.Gizmo;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public record SphereGizmo(Vec3 pos, float radius, GizmoStyle style) implements Gizmo {
    // Количество сегментов по горизонтали (долгота) и вертикали (широта)
    private static final int SEGMENTS = 16;

    @Override
    public void emit(final @NonNull GizmoPrimitives primitives, final float alphaMultiplier) {
        if (!this.style.hasStroke() && !this.style.hasFill()) {
            return;
        }

        // Предвычисляем сетку вершин сферы
        // SEGMENTS + 1 по горизонтали (чтобы замкнуть круг) и по вертикали от полюса до полюса
        Vec3[][] grid = new Vec3[SEGMENTS + 1][SEGMENTS + 1];

        for (int i = 0; i <= SEGMENTS; i++) {
            // v от 0 до PI (широта, от верхнего полюса к нижнему)
            float v = i * (float) Math.PI / SEGMENTS;
            float sinV = (float) Math.sin(v);
            float cosV = (float) Math.cos(v);

            for (int j = 0; j <= SEGMENTS; j++) {
                // u от 0 до 2*PI (долгота, полный круг)
                float u = j * 2.0f * (float) Math.PI / SEGMENTS;
                float sinU = (float) Math.sin(u);
                float cosU = (float) Math.cos(u);

                // Вычисляем смещение от центра сферы
                double x = this.radius * sinV * cosU;
                double y = this.radius * cosV; // Ось Y направлена вверх в Minecraft
                double z = this.radius * sinV * sinU;

                grid[i][j] = this.pos.add(x, y, z);
            }
        }

        // 1. Рисуем заливку (полигоны)
        if (this.style.hasFill()) {
            int color = this.style.multipliedFill(alphaMultiplier);

            for (int i = 0; i < SEGMENTS; i++) {
                for (int j = 0; j < SEGMENTS; j++) {
                    Vec3 a = grid[i][j];
                    Vec3 b = grid[i + 1][j];
                    Vec3 c = grid[i + 1][j + 1];
                    Vec3 d = grid[i][j + 1];

                    // addQuad идеально подходит для рисования полигонов сетки сферы
                    primitives.addQuad(a, b, c, d, color);
                }
            }
        }

        // 2. Рисуем каркас (линии сетки)
        if (this.style.hasStroke()) {
            int color = this.style.multipliedStroke(alphaMultiplier);
            float width = this.style.strokeWidth();

            for (int i = 0; i <= SEGMENTS; i++) {
                for (int j = 0; j < SEGMENTS; j++) {
                    // Горизонтальные линии (параллели)
                    primitives.addLine(grid[i][j], grid[i][j + 1], color, width);

                    // Вертикальные линии (меридианы)
                    if (i < SEGMENTS) {
                        primitives.addLine(grid[i][j], grid[i + 1][j], color, width);
                    }
                }
            }
        }
    }
}